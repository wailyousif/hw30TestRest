package com.ironyard.controller.rest;

import com.ironyard.data.Game;
import com.ironyard.dto.ResponseObject;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wailm.yousif on 3/8/17.
 */

@RestController
@RequestMapping(path = "/rest/cards")
public class WarRestController
{
    private static final String emptyString = "";

    //@Autowired
    //WarRestController() { }

    @RequestMapping(value = "/startgame", method = RequestMethod.GET)
    public ResponseObject startGame(@RequestParam(value = "numberOfPlayers", required = false) Integer numberOfPlayers)
    {
        ResponseObject responseObject = new ResponseObject(false, -10, "Failed to create a new game", null);
        try
        {
            if (numberOfPlayers == null)
            {
                responseObject.setResponseCode(-20);
                throw new Exception("Number of players can't be null.");
            }
            else if (numberOfPlayers < 2)
            {
                throw new Exception("Number of players must be at least 2.");
            }

            Game aGame = new Game();
            aGame.initGame(numberOfPlayers);
            aGame.dealOutAllCards();

            responseObject.setSuccess(true);
            responseObject.setResponseCode(0);
            responseObject.setResponseString(emptyString);
            responseObject.setGame(aGame);
        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            System.out.println("Exception in startGame Rest: " + err);
            responseObject.setResponseString(err);
        }

        return responseObject;
    }



    @RequestMapping(value = "/turn", method = RequestMethod.POST)
    public ResponseObject turn(@RequestBody(required = true) Game aGame)
    {
        ResponseObject responseObject = new ResponseObject(false, -100,
                "Failed to play players' round", aGame);

        try
        {
            if (aGame.getWinner() != null)
            {
                responseObject.setResponseCode(-110);
                throw new Exception("Game over, no more rounds are allowed. Winner is: " + aGame.getWinner().getName());
            }

            aGame.playRound();

            responseObject.setSuccess(true);
            responseObject.setResponseCode(0);
            String winner = "n/a";
            if (aGame.getWinner() != null)
                winner = aGame.getWinner().getName();

            /*
            if (aGame.getWinner() == null && aGame.getTurns() >= aGame.ROUNDS_LIMIT)   //put limit so that game doesn't go indefinitely
            {
                //winner will be the one who has the greatest number of cards
                //in case of tie, play one more round to find the winner
                int highest = 0;
                int indexOfHighest = -1;
                for (int i=0; i < aGame.getPlayers().size(); i++)   //find first highest
                {
                    Player p = aGame.getPlayers().get(i);
                    int totalCards = p.getWonCards().size() + p.getCardsToPlayWith().size();
                    if (totalCards > highest)
                    {
                        highest = totalCards;
                        indexOfHighest = i;
                    }
                }

                boolean tie = false;
                for (int i=0; i < aGame.getPlayers().size(); i++)   //search for ties
                {
                    if (i == indexOfHighest)
                        continue;
                    Player p = aGame.getPlayers().get(i);
                    int totalCards = p.getWonCards().size() + p.getCardsToPlayWith().size();
                    if (totalCards == highest)  //there is a tie
                    {
                        tie = true;
                        break;
                    }
                }

                if (!tie)
                {
                    aGame.setWinner(aGame.getPlayers().get(indexOfHighest));
                    winner = aGame.getWinner().getName();
                }
            }
            */

            responseObject.setResponseString("Number of rounds played: " + String.valueOf(aGame.getTurns()) +
                    ". Winner: " + winner);
            responseObject.setGame(aGame);
        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            System.out.println("Exception in turn rest: " + err);
            responseObject.setResponseString(err);
        }

        return responseObject;
    }


    @RequestMapping(value = "/simulatefullgame", method = RequestMethod.GET)
    public ResponseObject simulatefullgame(@RequestParam(value = "numberOfPlayers", required = false) Integer numberOfPlayers)
    {
        ResponseObject responseObject = new ResponseObject(false, -10, "", null);

        try
        {
            if (numberOfPlayers == null)
            {
                responseObject.setResponseCode(-20);
                throw new Exception("Number of players can't be null.");
            }
            else if (numberOfPlayers < 2)
            {
                throw new Exception("Number of players must be at least 2.");
            }

            Game aGame = new Game();
            aGame.initGame(numberOfPlayers);
            aGame.dealOutAllCards();

            while (aGame.getWinner() == null)
            {
                aGame.playRound();
            }

            responseObject.setSuccess(true);
            responseObject.setResponseCode(0);
            responseObject.setResponseString("Game Over. Winner is: " + aGame.getWinner().getName());
            responseObject.setGame(aGame);
        }
        catch (Exception ex)
        {
            String err = ex.getMessage();
            System.out.println("Exception in simulatefullgame rest: " + err);
            responseObject.setResponseCode(-150);   //code for unknown error
            responseObject.setResponseString(err);
        }

        return responseObject;
    }
}
