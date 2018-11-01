package br.com.andersonsv.recipe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import br.com.andersonsv.recipe.R;
import br.com.andersonsv.recipe.adapter.RecipeRecyclerViewAdapter;
import br.com.andersonsv.recipe.data.Recipe;
import br.com.andersonsv.recipe.network.RecipeService;
import br.com.andersonsv.recipe.network.RetrofitClientInstance;
import br.com.andersonsv.recipe.util.NetworkUtils;
import br.com.andersonsv.recipe.util.UiUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static br.com.andersonsv.recipe.util.Extras.EXTRA_RECIPE_LIST;
public class MainActivity extends AppCompatActivity implements RecipeRecyclerViewAdapter.RecipeRecyclerOnClickHandler {

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.rvRecipe)
    RecyclerView mRvRecipe;
    @BindView(R.id.pgLoading)
    ProgressBar mLoading;
    @BindView(R.id.mainContext)
    ConstraintLayout mMainContext;

    private RecipeRecyclerViewAdapter mRecipeAdapter;

    private List<Recipe> mRecipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mRecipeAdapter = new RecipeRecyclerViewAdapter(this);
        mRvRecipe.setAdapter(mRecipeAdapter);

        int mNoOfColumns = UiUtils.getColumnForDimension(this);
        GridLayoutManager glm = new GridLayoutManager(this, mNoOfColumns);

        mRvRecipe.setLayoutManager(glm);
        mRvRecipe.setHasFixedSize(true);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_RECIPE_LIST)) {
                List<Recipe> recipes = savedInstanceState.getParcelableArrayList(EXTRA_RECIPE_LIST);
                mRecipeList = recipes;
                mRecipeAdapter.swapData(recipes);
            }
        } else {
            if (!NetworkUtils.isNetworkConnected(this)) {
                // retry connect
                createSnackbarToReload(R.string.offline_no_internet);
            } else {
                loadData();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipeList != null && !mRecipeList.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_RECIPE_LIST, new ArrayList<>(mRecipeList));
        }
    }

    private void loadData() {

        RecipeService service = RetrofitClientInstance.getRetrofitInstance().create(RecipeService.class);

        Call<ArrayList<Recipe>> call = service.getRecipes();

        mLoading.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
                Log.i(TAG, "SUCCESS " + response.code());
                Log.i(TAG, "RESPONSE" + response.body());


                mRecipeAdapter.swapData(response.body());
                mRecipeList = mRecipeAdapter.getData();

                mLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                mLoading.setVisibility(View.INVISIBLE);
                createSnackbarToReload(R.string.error_download_data);
                Log.e(TAG, "ERROR " + t.getLocalizedMessage());
            }
        });
    }

    private void createSnackbarToReload(int textResource) {
        Snackbar snackbar = Snackbar.make(mMainContext, textResource, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry_download_data, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadData();
                    }
                });
        snackbar.show();
    }

    @Override
    public void onClick(Recipe recipe) {
        Class destiny = RecipeActivity.class;

        Intent intent = new Intent(MainActivity.this, destiny);
        intent.putExtra(Intent.EXTRA_INTENT, recipe);

        startActivity(intent);
    }
}
