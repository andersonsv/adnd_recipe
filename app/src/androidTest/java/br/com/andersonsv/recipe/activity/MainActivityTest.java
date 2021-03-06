package br.com.andersonsv.recipe.activity;


import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.andersonsv.recipe.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {


    private IdlingResource mIdlingResource;

    @Rule
    public final ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void mainTest() {
        int RECYCLER_VIEW_FIRST_ITEM = 0;
        String NUTELLA_PIE = "NUTELLA PIE";
        String NUTELLA_PIE_STEP = "Recipe Introduction";
        onView(withId(R.id.rvRecipe)).check(matches(hasDescendant(withText(NUTELLA_PIE))));
        //onView(withId(R.id.rvRecipe)).perform(RecyclerViewActions.actionOnItemAtPosition(RECYCLER_VIEW_FIRST_ITEM, click()));
        //onView(withText(NUTELLA_PIE_STEP)).check(matches(isDisplayed()));


    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
