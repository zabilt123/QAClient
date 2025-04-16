package com.example.qaclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CommunicateActivity extends AppCompatActivity {

    private Socket socket = null;
    private PrintWriter out = null;
    private Scanner in = null;
    private TextView tv;

    // In onCreate, connect to the server, and then wait for the
    // user to input the question and press the button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_communicate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int port;
        String hostname;

        // Get the hostname from the intent

        Intent intent = getIntent();
        hostname = intent.getStringExtra(MainActivity.HOST_NAME);

        // Get the port from the intent.  Default port is 4000

        port = intent.getIntExtra(MainActivity.PORT, 4000);

        // get a handle on the TextView for displaying the status

        tv = (TextView) findViewById(R.id.text_answer);

        // Try to open the connection to the server

        try
        {
            socket = new Socket(hostname, port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(new InputStreamReader(socket.getInputStream()));

            tv.setText("Connected to server.");
        }
        catch (IOException e)  // socket problems
        {
            tv.setText("Problem: " + e.toString());
            socket = null;
        }

    } // end onCreate

    public void sendQuestion(View view)
    {
        EditText et;
        String user_question;
        String answer;
        boolean finished = false;

        // are we connected?

        if(socket == null)
        {
            tv.setText("Not connected.");
        }
        else
        {

            // get the question to send to the server (place it in "user_question")
            et = findViewById(R.id.edit_question);

            user_question = et.getText().toString();

            // if the (input) question is "quit", we're finished; let
            // the server know by sending it "quit".  Also, don't forget
            // to "raise the flag" locally.  Otherwise, just send the
            // question and get a response

            if ( user_question.equalsIgnoreCase("quit") /* YYY */ ) // YYY: write proper condition: replace "true"
            {
                    out.println("quit");
                finished = true;// YYY (two statements here)
            }

            else
            {


                // send question to server
                out.println(user_question);
                // YYY
                // read response (into answer) and display it
                if (in.hasNextLine()) {
                    answer = in.nextLine();
                    tv.setText("Answer: " + answer);
                }
                else{
                    tv.setText("Invalid input");
                }



            }

            // if we're finished, close the connection

            if(finished)
            {
                try
                {
                    out.close();
                    in.close();
                    socket.close();

                // set socket back to null to indicate that we're disconnected

                    socket = null;

                    tv.setText("Finished.  Connection closed.");
                }
                catch (IOException e)  // socket problems
                {
                    tv.setText("Problem: " + e.toString());
                }

            }

        }

    } // end sendQuestion

} // end CommunicateActivity
