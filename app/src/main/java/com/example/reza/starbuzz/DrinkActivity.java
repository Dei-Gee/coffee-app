package com.example.reza.starbuzz;
/*

There are a number of steps we need to go through to change DrinkActivity so that it uses the Starbuzz database:

1. Get a reference to the Starbuzz database.

We’ll do this using the Starbuzz SQLite helper we created in the previous version of this app.

2. Create a cursor to read drink data from the database.

We need to read the data held in the Starbuzz database for the drink the user selects in DrinkCategoryActivity.
The cursor will give us access to this data.

3. Navigate to the drink record.

Before we can use the data retrieved by the cursor, we need to explicitly navigate to it.

4. Display details of the drink in DrinkActivity.

Once we’ve navigated to the drink record in the cursor, we need to read the data and display it in DrinkActivity.

 */

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class DrinkActivity extends Activity {

    public static final String EXTRA_DRINKID = "drinkId";

    @Override
    /*
    The onCreate() method gets the drink ID selected by the user,
    gets the details of that drink from the Drink class,
    and then populates the activity’s views using the drink attributes.

    We need to change the code in the onCreate() method to get the data from the Starbuzz database.
    The first thing we need is to get a reference to the Starbuzz database using the SQLite helper
    we created in the previous app.

    You then call the SQLite helper’s getReadableDatabase() or getWritableDatabase()
    to get a reference to the database.

    You use the getReadableDatabase() method if you need read-only access to the database,
    and the getWritableDatabase() method if you need to perform any updates.

    Both methods return a SQLiteDatabase object that you can use to access the database.

    If Android fails to get a reference to the database, a SQLiteException is thrown.

    This can happen if, for example, you call the getWritableDatabase() to
    get read/write access to the database, but you can’t write to the database because the disk is full.

    In our particular case, we only need to read data from the database,
    so we’ll use the getReadableDatabase() method.

    If Android can’t get a reference to the database and a SQLiteException is thrown,
    we’ll use a Toast (a pop-up message) to tell the user that the database is unavailable.

    Once you have a reference to the database, you can get data from it using a cursor.

    A cursor lets you read from and write to the database.

    You specify what data you want access to, and the cursor brings back the relevant records from the database.
    You can then navigate through the records supplied by the cursor.

    You create a cursor using a database query.
    A database query lets you specify which records you want access to from the database.
    As an example, you can say you want to access all the records from the DRINK table,
    or just one particular record. These records are then returned in the cursor.

    You create a cursor using the SQLiteDatabase query() method.

    There are many overloaded versions of this method with different parameters,
    so rather than go into each variation, we’re only going to describe the most common ways of using it.

    The simplest type of database query is one that returns all the records from a particular table in the database.
    This is useful if, for instance, you want to display all the records in a list in an activity.

    To return all the records from a particular table,
    you pass the name of the table as the query() method’s first parameter,
    and a String array of the column names as the second.

    You set all of the other parameters to null, as you don’t need them for this type of query.

    You can filter your data by declaring conditions the data must meet.
    As an example, here’s how you’d return records from the DRINK table where the name of the drink is “Latte”:

    Cursor cursor = db.query("DRINK",
                       new String[] {"_id", "NAME", "DESCRIPTION"},
                       "NAME = ?",
                       new String[] {"Latte"},
                       null, null, null);

     Note that lines 102 and 102 statements indicate that we want to return records where the value of the NAME
     column is "Latte".

     The third and fourth parameters in the query describe the conditions the data must meet.

     The third parameter specifies the column in the condition.
     In the above example we want to return records where the value of the NAME column is “Latte”,
     so we use "NAME = ?". We want the value in the NAME column to be equal to some value,
     and the ? symbol is a placeholder for this value.

    The fourth parameter is an array of Strings that specifies what the value of the condition should be.
    In the above example we want to update records where the value of the NAME column is “Latte”, so we use:

    new String[] {"Latte"};
    The value of the condition must be an array of Strings,
    even if the column you’re applying the condition to contains some other type of data.

    As an example, in the code below we return records from the DRINK table where the drink _id is drinkId.

    Whenever you need to retrieve values from a particular record in a cursor,
    you first need to navigate to that record.

    In our particular case,
    we have a cursor that’s composed of a single record that contains details of the drink the user selected.
    We need to navigate to that record in order to read details of the drink.

    There are four main methods you use to navigate through the records in a cursor:
    moveToFirst(), moveToLast(), moveToPrevious(), and moveToNext().

    To go to the first record in a cursor, you use its moveToFirst() method.
    This method returns a value of true if it finds a record,
    and false if the cursor hasn’t returned any records.

    To go to the last record, you use the moveToLast() method.
    Just like the moveToFirst() method, it returns a value of true if it finds a record,
    and false if it doesn’t.

    To move through the records in the cursor, you use the moveToPrevious() and moveToNext() methods.

    The moveToPrevious() method moves you to the previous record in the cursor.
    It returns true if it succeeds in moving to the previous record,
    and false if it fails (for example, if it’s already at the first record).

    The moveToNext() method works in a similar way to the moveToPrevious() method,
    except that it moves you to the next record in the cursor instead.

    In our case, we want to read values from the first (and only) record in the cursor,
    so we’ll use the moveToFirst() method to navigate to this record.

    Once you’ve navigated to a record in your cursor, you can access its values.

    You retrieve values from a cursor’s current record using the cursor’s get*() methods:
    getString(), getInt(), and so on.
    The exact method you use depends on the type of value you want to retrieve.
    To get a String value, for example, you’d use the getString() method,
    and to get an int value you’d use getInt().

    Each of the methods takes a single parameter:
    the index of the column whose value you want to retrieve, starting at 0.

    Once you’ve finished retrieving values from the cursor,
    you need to close the cursor and the database in order to release their resources.
    You do this by calling the cursor and database close() methods.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        int drinkId = (Integer) getIntent().getExtras().get(EXTRA_DRINKID);

        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try {
            //Get a refernce to the database
            SQLiteDatabase db = starbuzzDatabaseHelper.getReadableDatabase();
            //Create a cursor to get the name, description, and image resource ID of the drink the user selected
            Cursor cursor = db.query("DRINK",
                    new String[] {"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID"},
                    "_id = ?",
                    new String[] {Integer.toString(drinkId)},
                    null, null, null);
            //There is only one record in the cursor, but we still need to move to it
            if (cursor.moveToFirst()){
                /*
                The name of the drink is the first item in the cursor, the description is the second column,
                and the image resource ID is the third. That's because we told the cursor to use the NAME,
                DESCRIPTION, and IMAGE_RESOURCE_ID columns from the database in that order.
                 */
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int photoId = cursor.getInt(2);
                TextView name = (TextView) findViewById(R.id.name);
                name.setText(nameText);
                TextView description = (TextView) findViewById(R.id.description);
                description.setText(descriptionText);
                ImageView photo = (ImageView) findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);
            }
            //close the cursor and database
            cursor.close();
            db.close();
        }catch (SQLiteException e){
            //display a pop-up message if a SQLiteException is thrown
            Toast toast = Toast.makeText(this, "Database Unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
