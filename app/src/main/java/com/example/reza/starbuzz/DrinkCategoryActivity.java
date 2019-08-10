package com.example.reza.starbuzz;
/*
We need to update the code in DrinkCategoryActivity so that it uses data from the database rather than
from the Java Drink class.

When we updated DrinkActivity to get it to read data from the Starbuzz database,
we created a cursor to read data for the drink the user selected,
and then we used the values from the cursor to update DrinkActivity’s views.

The steps we need to go through to update DrinkCategoryActivity are slightly different.
This is because DrinkCategoryActivity displays a list view that uses the drink data as its source.

We need to switch the source of this data to be the Starbuzz database.

Here are the steps we need to go through to change DrinkCategoryActivity so that it uses the Starbuzz database:

1. Create a cursor to read drink data from the database.

As before, we need to get a reference to the Starbuzz database.
Then we’ll create a cursor to retrieve the drink names from the DRINK table.

2. Replace the list view’s array adapter with a cursor adapter.

The list view currently uses an array adapter to get its drink names.
This is because the data’s held in an array in the Drink class.
Because we’re now accessing the data using a cursor, we’ll use a cursor adapter instead.

A cursor adapter is just like an array adapter,
except that instead of getting its data from an array, it reads the data from a cursor.

ListViews and Spinners can use any subclass of the Adapter class for their data.
This includes ArrayAdapter, CursorAdapter, and SimpleCursorAdapter (a subclass of CursorAdapter).

 */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DrinkCategoryActivity extends Activity {
    private SQLiteDatabase db;
    private Cursor cursor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_category);
        ListView listDrinks = (ListView) findViewById(R.id.list_drinks);
        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try {
            //We get a reference to the database in exactly the same way that we did in DrinkActivity class
            db = starbuzzDatabaseHelper.getReadableDatabase();
            /*
            To create the cursor, we need to specify what data we want it to contain.
            We want to use the cursor to display a list of drink names,
            so the cursor needs to include the NAME column.
            We’ll also include the _id column to get the ID of the drink.
            we need to pass the ID of the drink the user chooses to DrinkActivity
            so that DrinkActivity can display its details. Here’s the cursor:
             */
            cursor = db.query("DRINK", new String[] {"_id", "NAME"}, null, null,
                    null, null, null );

            /*
            We’re going to create a simple cursor adapter to display a list of drink names from the DRINK table.
            To do this, we’ll create a new instance of the SimpleCursorAdapter class,
            passing in parameters to tell the adapter what data to use and how it should be displayed.
            Finally, we’ll assign the adapter to the list view.

            Here is the constructor for SimpleCursorAdapter class:

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(Context context,
                                                                  int layout,
                                                                  Cursor cursor,
                                                                  String[] fromColumns,
                                                                  int[] toViews,
                                                                  int flags);


            The context and layout parameters are exactly the same ones you used when you created an array adapter:
            context is the current context,
            and layout says how you want to display the data.
            Instead of saying which array we need to get our data from,
            we need to specify which cursor contains the data.
            You then use fromColumns to specify which columns in the cursor you want to use,
            and toViews to say which views you want to display them in.

            The flags parameter is usually set to 0, which is the default.
            The alternative is to set it to FLAG_REGISTER_CONTENT_OBSERVER to register
            a content observer that will be notified when the content changes.

            Note:Any cursor you use with a cursor adapter MUST include the _id column or it won’t work.

            So in the following, the first argument in SimpleCursorAdapter constructor is the current activity,
            the next says how to display the data, the next is the cursor you create and should include the _id
            column and the data you want to appear, the next two arguments say which columns in the cursor to
            match to which views and the last argument is used to determine the behaviour of the cursor
             */
            SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    cursor,
                    //Displays the contents of the NAME column in the ListView textviews
                    new String[]{"NAME"},
                    new int[]{android.R.id.text1},
                    0);
            listDrinks.setAdapter(listAdapter);//use setAdapter() to connect the adapter to the ListView
        }catch (SQLiteException e){
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent (DrinkCategoryActivity.this, DrinkActivity.class);
                        intent.putExtra(DrinkActivity.EXTRA_DRINKID, (int)id);
                        startActivity(intent);
                    }
                };
        listDrinks.setOnItemClickListener(itemClickListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
