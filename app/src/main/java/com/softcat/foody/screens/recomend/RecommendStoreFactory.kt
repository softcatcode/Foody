package com.softcat.foody.screens.recomend

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.softcat.domain.entities.Ingredient
import com.softcat.domain.entities.Recipe
import com.softcat.domain.entities.RecipeTag
import com.softcat.domain.entities.User
import com.softcat.domain.usecases.FavouritesUseCase
import com.softcat.domain.usecases.IngredientUseCase
import com.softcat.domain.usecases.RecipeTagUseCase
import com.softcat.domain.usecases.RecipeUseCase
import com.softcat.domain.usecases.ScoreUseCase
import com.softcat.domain.usecases.UserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RecommendStoreFactory
    @Inject
    constructor(
        private val storeFactory: StoreFactory,
        private val recipeUseCase: RecipeUseCase,
        private val favouritesUseCase: FavouritesUseCase,
        private val userUseCase: UserUseCase,
        private val scoreUseCase: ScoreUseCase,
        private val ingredientUseCase: IngredientUseCase,
        private val tagUseCase: RecipeTagUseCase,
    ) {
        fun create(lifecycle: Lifecycle): RecommendStore =
            object :
                RecommendStore,
                Store<RecommendStore.Intent, RecommendStore.State, RecommendStore.Label> by
                storeFactory.create(
                    name = this::class.simpleName,
                    initialState =
                        RecommendStore.State(
                            ingredients = emptyList(),
                            tags = emptyList(),
                            maxAbsentIngredients = 0,
                            resultStatus = RecommendStore.State.RecommendationStatus.Initial,
                            ingredientDialogState = RecommendStore.State.SelectIngredientDialogState.Hidden,
                            tagDialogState = RecommendStore.State.SelectTagDialogState.Hidden,
                        ),
                    executorFactory = { RecommendationsExecutor(lifecycle) },
                    reducer = RecommendReducer,
                ) {}

        sealed interface Msg {
            data class AddIngredient(val elem: Ingredient) : Msg
            data class RemoveIngredient(val name: String) : Msg
            data class AddTag(val elem: RecipeTag) : Msg
            data class RemoveTag(val name: String) : Msg
            data class ChangeMaxAbsentIngredients(val newValue: Int) : Msg
            data class ChangeSearchIngredientQuery(val newValue: String) : Msg
            data class ChangeSearchTagQuery(val newValue: String) : Msg
            data object RecommendationLoading : Msg
            data class RecommendationReady(val recipes: List<RecipeRecommendationModel>) : Msg
            data object ShowAddRequiredIngredientDialog : Msg
            data object ShowAddRequiredTagDialog : Msg
            data object HideDialog : Msg

            data class SearchIngredientResult(
                val query: String,
                val ingredients: List<Ingredient>
            ): Msg

            data class SearchTagResult(
                val query: String,
                val tags: List<RecipeTag>
            ): Msg
        }

    private inner class RecommendationsExecutor(
        lifecycle: Lifecycle
    ): CoroutineExecutor<RecommendStore.Intent, Nothing, RecommendStore.State, Msg, RecommendStore.Label>() {
        private var currentUser: User? = null
        private var recommendation = emptyList<Recipe>()
        private var favouriteRecipesIds: Set<Int>? = null
        private val selectedIngredients = mutableListOf<Ingredient>()
        private val selectedTags = mutableListOf<RecipeTag>()

        private var userCollectingJob: Job? = null
        private var favouritesCollectingJob: Job? = null

        init {
            lifecycle.doOnStart {
                userCollectingJob = scope.launch {
                    userUseCase.observeLastEnteredUser().collect(::lastEnteredUserCollector)
                }
            }
            lifecycle.doOnStop {
                userCollectingJob?.cancel()
                favouritesCollectingJob?.cancel()
            }
        }

        override fun executeIntent(intent: RecommendStore.Intent) {
            Timber.i("${this::class.simpleName}: Intent is obtained: $intent")
            when (intent) {
                is RecommendStore.Intent.AddIngredient -> {
                    if (intent.elem !in selectedIngredients)
                        selectedIngredients.add(intent.elem)
                    dispatch(Msg.AddIngredient(intent.elem))
                }

                is RecommendStore.Intent.AddTag -> {
                    if (intent.elem !in selectedTags)
                        selectedTags.add(intent.elem)
                    dispatch(Msg.AddTag(intent.elem))
                }

                is RecommendStore.Intent.RemoveIngredient -> {
                    selectedIngredients.removeIf { it.name == intent.name }
                    dispatch(Msg.RemoveIngredient(intent.name))
                }

                is RecommendStore.Intent.RemoveTag -> {
                    selectedTags.removeIf { it.name == intent.name }
                    dispatch(Msg.RemoveTag(intent.name))
                }

                is RecommendStore.Intent.OpenRecipeDetails -> {
                    val recipe = recommendation.find { it.id == intent.id } ?: return
                    publish(RecommendStore.Label.OpenRecipeDetails(recipe))
                }

                is RecommendStore.Intent.ChangeSearchIngredientQuery -> dispatch(Msg.ChangeSearchIngredientQuery(intent.vewValue))
                is RecommendStore.Intent.ChangeSearchTagQuery -> dispatch(Msg.ChangeSearchTagQuery(intent.vewValue))
                is RecommendStore.Intent.SearchIngredient -> searchIngredient(intent.query)
                is RecommendStore.Intent.SearchTag -> searchTag(intent.query)
                is RecommendStore.Intent.ChangeFavouriteStatus -> changeFavouriteStatus(intent.recipeId, intent.isFavourite)
                RecommendStore.Intent.Recommend -> makeRecipesRecommendation()
                RecommendStore.Intent.ShowAddRequiredIngredientDialog -> dispatch(Msg.ShowAddRequiredIngredientDialog)
                RecommendStore.Intent.ShowAddRequiredTagDialog -> dispatch(Msg.ShowAddRequiredTagDialog)
                RecommendStore.Intent.HideDialog -> dispatch(Msg.HideDialog)
                is RecommendStore.Intent.ChangeMaxAbsentIngredients -> dispatch(Msg.ChangeMaxAbsentIngredients(intent.newValue))
            }
        }

        private fun lastEnteredUserCollector(newUser: User?) {
            currentUser = newUser
            newUser?.id?.let { userId ->
                favouritesCollectingJob = scope.launch(Dispatchers.IO) {
                    favouritesUseCase.observeFavouriteIds(userId).collect {
                        withContext(Dispatchers.Main) {
                            updateFavourites(it)
                        }
                    }
                }
            }
        }

        private fun changeFavouriteStatus(
            recipeId: Int,
            isFavourite: Boolean,
        ) {
            val userId = currentUser?.id ?: return
            scope.launch(Dispatchers.IO) {
                if (isFavourite) {
                    favouritesUseCase.remove(userId, recipeId)
                } else {
                    favouritesUseCase.add(userId, recipeId)
                }
            }
        }

        private fun makeRecipesRecommendation() {
            val userId = currentUser?.id ?: return
            scope.launch(Dispatchers.Default) {
                val scores = scoreUseCase.observe(userId).first()
                val recipes = recipeUseCase.recommend(
                    scores = scores,
                    ingredients = selectedIngredients,
                    tags = selectedTags
                ).map { recipe ->
                    RecipeRecommendationModel(
                        id = recipe.id,
                        name = recipe.name,
                        description = recipe.description,
                        isFavourite = favouriteRecipesIds?.let { recipe.id in it } ?: false,
                        isFavouriteVisible = favouriteRecipesIds != null,
                    )
                }
                withContext(Dispatchers.Main) {
                    dispatch(Msg.RecommendationReady(recipes))
                }
            }
        }

        private fun searchIngredient(query: String) {
            scope.launch(Dispatchers.IO) {
                val result = ingredientUseCase.search(query)
                withContext(Dispatchers.Main) {
                    dispatch(Msg.SearchIngredientResult(query, result))
                }
            }
        }

        private fun searchTag(query: String) {
            scope.launch(Dispatchers.IO) {
                val result = tagUseCase.search(query)
                withContext(Dispatchers.Main) {
                    dispatch(Msg.SearchTagResult(query, result))
                }
            }
        }

        private fun updateFavourites(newFavouriteIds: Set<Int>) {
            favouriteRecipesIds = newFavouriteIds
            val status = state().resultStatus
                    as? RecommendStore.State.RecommendationStatus.Content ?: return
            val recipes = status.recipes.map { recipe ->
                recipe.copy(
                    isFavourite = favouriteRecipesIds?.let { recipe.id in it } ?: false,
                    isFavouriteVisible = favouriteRecipesIds != null,
                )
            }
            dispatch(Msg.RecommendationReady(recipes))
        }
    }

        private object RecommendReducer : Reducer<RecommendStore.State, Msg> {
            override fun RecommendStore.State.reduce(msg: Msg): RecommendStore.State {
                Timber.i("${this::class.simpleName}: Message is obtained: $msg")
                return when (msg) {
                    is Msg.AddIngredient -> {
                        copy(ingredients = ingredients.toMutableList().apply { add(msg.elem.name) })
                    }

                    is Msg.AddTag -> {
                        copy(tags = tags.toMutableList().apply { add(msg.elem.name) })
                    }

                    is Msg.ChangeMaxAbsentIngredients -> {
                        copy(maxAbsentIngredients = msg.newValue)
                    }

                    Msg.RecommendationLoading -> {
                        copy(resultStatus = RecommendStore.State.RecommendationStatus.Loading)
                    }

                    is Msg.RecommendationReady -> {
                        copy(resultStatus = RecommendStore.State.RecommendationStatus.Content(msg.recipes))
                    }

                    is Msg.RemoveIngredient -> {
                        val newValue = ingredients
                            .toMutableList()
                            .apply { removeIf { it == msg.name } }
                        copy(ingredients = newValue)
                    }

                    is Msg.RemoveTag -> {
                        val newValue = tags
                            .toMutableList()
                            .apply { removeIf { it == msg.name } }
                        copy(tags = newValue)
                    }

                    Msg.ShowAddRequiredIngredientDialog -> {
                        val state = RecommendStore.State.SelectIngredientDialogState.Shown(
                            query = "",
                            searchResult = emptyList()
                        )
                        copy(ingredientDialogState = state)
                    }

                    Msg.ShowAddRequiredTagDialog -> {
                        val state = RecommendStore.State.SelectTagDialogState.Shown(
                            query = "",
                            searchResult = emptyList()
                        )
                        copy(tagDialogState = state)
                    }

                    Msg.HideDialog -> {
                        copy(
                            ingredientDialogState = RecommendStore.State.SelectIngredientDialogState.Hidden,
                            tagDialogState = RecommendStore.State.SelectTagDialogState.Hidden
                        )
                    }

                    is Msg.ChangeSearchIngredientQuery -> {
                        if (ingredientDialogState is RecommendStore.State.SelectIngredientDialogState.Shown) {
                            copy(
                                ingredientDialogState = ingredientDialogState.copy(
                                    query = msg.newValue
                                )
                            )
                        } else
                            this
                    }

                    is Msg.ChangeSearchTagQuery -> {
                        if (tagDialogState is RecommendStore.State.SelectTagDialogState.Shown) {
                            copy(
                                tagDialogState = tagDialogState.copy(
                                    query = msg.newValue
                                )
                            )
                        } else
                            this
                    }

                    is Msg.SearchTagResult -> {
                        copy(
                            tagDialogState = RecommendStore.State.SelectTagDialogState.Shown(
                                query = msg.query,
                                searchResult = msg.tags
                            )
                        )
                    }

                    is Msg.SearchIngredientResult -> {
                        copy(
                            ingredientDialogState = RecommendStore.State.SelectIngredientDialogState.Shown(
                                query = msg.query,
                                searchResult = msg.ingredients
                            )
                        )
                    }
                }
            }
        }
    }
