package zoeller.john.systemics.edu.hangman;

import java.util.Random;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.GridView;

public class GameActivity extends AppCompatActivity {

    private String[] words;
    private Random rand;
    private String currWord;            // Variable to store the current game word
    private LinearLayout wordLayout;
    private TextView[] charViews;       // Views for each letter in the answer
    private GridView letters;
    private LetterAdapter ltrAdapt;

    private AlertDialog helpAlert;

    //body part images
    private ImageView[] bodyParts;
    //number of body parts
    private int numParts=6;
    //current part - will increment when wrong answers are chosen
    private int currPart;
    //number of characters in current word
    private int numChars;
    //number correctly guessed
    private int numCorr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Turn on the action bar
        /*
        This currently causes an ERROR on the PHONE
        it causes the app to crash after the PLAY BUTTON is pushed
        and generates a system error
        This is used to access the HELP screen
         */
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        // Read answer words from the array
        Resources res = getResources();
        words = res.getStringArray(R.array.words);

        rand = new Random();        // Get a new random word
        currWord = "";              // Clear out the current word variable

        wordLayout = (LinearLayout)findViewById(R.id.word);     //

        letters = (GridView)findViewById(R.id.letters);

        bodyParts = new ImageView[numParts];
        bodyParts[0] = (ImageView)findViewById(R.id.head);
        bodyParts[1] = (ImageView)findViewById(R.id.body);
        bodyParts[2] = (ImageView)findViewById(R.id.arm1);
        bodyParts[3] = (ImageView)findViewById(R.id.arm2);
        bodyParts[4] = (ImageView)findViewById(R.id.leg1);
        bodyParts[5] = (ImageView)findViewById(R.id.leg2);

        playGame();

    }

    private void playGame() {

        currPart = 0;                   // Set the current body part to 0 (head)
        numChars = currWord.length();   // Set the number of characters to the length of the current word
        numCorr = 0;                    //

        // Hide the body parts at the beginning of the game
        for (int p = 0; p < numParts; p++) {
        bodyParts[p].setVisibility(View.INVISIBLE);
        }

        //Choose a new word from the array at random
        String newWord = words[rand.nextInt(words.length)];

        // Make sure not to choose the same word twice
        while(newWord.equals(currWord)) newWord = words[rand.nextInt(words.length)];

        //Set the current word to the selected newWord
        currWord = newWord;

        // Create views for the characters in currWord
        charViews = new TextView[currWord.length()];

        //Remove all views
        wordLayout.removeAllViews();

        // Iterate over each letter of the answer,
        // Create a text view for each letter,
        // Set the text view's text to the current letter
        // and set the display properties on the text view and add it to the layout.
        for (int c = 0; c < currWord.length(); c++) {
            charViews[c] = new TextView(this);
            charViews[c].setText("" + currWord.charAt(c));
            charViews[c].setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            charViews[c].setGravity(Gravity.CENTER);
            charViews[c].setTextColor(Color.WHITE);
            charViews[c].setBackgroundResource(R.drawable.letter_bg);
            //add to layout
            wordLayout.addView(charViews[c]);
        }

        // Reset adapter
        ltrAdapt=new LetterAdapter(this);
        letters.setAdapter(ltrAdapt);

        }

    public void letterPressed(View view) {
    //user has pressed a letter to guess

        // Determine which character button the user has pressed
        // First, capture the the character button input from the screen
        String ltr=((TextView)view).getText().toString();

        // Next, take only the first character from the string
        char letterChar = ltr.charAt(0);

        view.setEnabled(false);                             // Turn off the each letter as it is guessed
        view.setBackgroundResource(R.drawable.letter_dn);   // Change the background

        boolean correct = false;

        for(int k = 0; k < currWord.length(); k++) {
            if (currWord.charAt(k) == letterChar) {
                correct = true;
                numCorr++;
                charViews[k].setTextColor(Color.BLACK);
            }
        }

        if (correct) {
            //correct guess
        }

        else if (currPart < numParts) {
            //some guesses left
            bodyParts[currPart].setVisibility(View.VISIBLE);
            currPart++;
        }

        else{
            // User has lost, call disableBtns method
            disableBtns();

            // User has lost, display Alert Dialog
            AlertDialog.Builder loseBuild = new AlertDialog.Builder(this);
            loseBuild.setTitle("OOPS");
            loseBuild.setMessage("You lose!\n\nThe answer was:\n\n"+currWord);
            loseBuild.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GameActivity.this.playGame();
                    }});

            loseBuild.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GameActivity.this.finish();
                    }});

            loseBuild.show();
        }

        if (numCorr == numChars) {
            // Disable Buttons
            disableBtns();

            // Display Alert Dialog
            AlertDialog.Builder winBuild = new AlertDialog.Builder(this);
            winBuild.setTitle("YAY");
            winBuild.setMessage("You win!\n\nThe answer was:\n\n" + currWord);
            winBuild.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GameActivity.this.playGame();
                    }});

            winBuild.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    GameActivity.this.finish();
                    }});

            winBuild.show();
        }
    }

    public void disableBtns() {
        int numLetters = letters.getChildCount();
        for (int l = 0; l < numLetters; l++) {
            letters.getChildAt(l).setEnabled(false);
            }
    }

    public void showHelp() {
        AlertDialog.Builder helpBuild = new AlertDialog.Builder(this);

        helpBuild.setTitle("Help");
        helpBuild.setMessage("Guess the word by selecting the letters.\n\n" + "You only have 6 wrong selections then it's game over!");
        helpBuild.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                helpAlert.dismiss();
                }});
        helpAlert = helpBuild.create();

        helpBuild.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_help:
                showHelp();
                return true;
            }

        return super.onOptionsItemSelected(item);
    }

}