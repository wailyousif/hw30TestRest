package com.ironyard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasonskipper on 10/20/16.
 */


public class Game {

    public static final Integer ROUNDS_LIMIT = 200;

    private List<Player> players;
    private List<Card> cardsInPlay;
    private List<Card> startingDeck;
    int turns = 0;

    private Player winner;

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Game()
    {

    }

    public Game(List<Player> players, List<Card> cardsInPlay, List<Card> startingDeck, int turns) {
        this.players = players;
        this.cardsInPlay = cardsInPlay;
        this.startingDeck = startingDeck;
        this.turns = turns;
    }



    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Card> getCardsInPlay() {
        return cardsInPlay;
    }

    public void setCardsInPlay(List<Card> cardsInPlay) {
        this.cardsInPlay = cardsInPlay;
    }

    public List<Card> getStartingDeck() {
        return startingDeck;
    }

    public void setStartingDeck(List<Card> startingDeck) {
        this.startingDeck = startingDeck;
    }

    public int getTurns() {
        return turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    //ExecutorService executor;

    /*
    public static void main(String[] args){
        Game g = new Game();
        g.initGame(4);
        g.dealOutAllCards();
        Player winner = g.startGame();

        System.out.printf("\n\n%s is the Winner! In %s turns\n", winner.getName(),g.turns);
    }
    */

    public void initGame(int numberOfPlayers){

        //executor = Executors.newFixedThreadPool(numberOfPlayers);
        //executor = Executors.newWorkStealingPool(numberOfPlayers);
        //executor = Executors.newWorkStealingPool();

        // set all lists to new empty lists
        turns = 0;
        players = new ArrayList<>();
        cardsInPlay = new ArrayList<>();
        startingDeck = new ArrayList<>();

        // create x players add to list of players
        for(int i=0; i<numberOfPlayers; i++){
            players.add(new Player("Player #"+i));
        }

        // init deck of cards
        startingDeck = Card.createDeck();

        // shuffle deck of cards
        shuffle(startingDeck);

    }

    public void dealOutAllCards(){
        // go through staringDeck
        int playerTurn = 0;
        for(Card c: startingDeck) {
            // give each player 1 card (repeat)
            int pos = playerTurn % players.size();
            players.get(pos).acceptCardForPlay(c);
            playerTurn++;
        }

    }

    //ReentrantLock lock = new ReentrantLock();


    private Integer onePlayerPlayCard(Player p)
    {
        Card t = p.playCard();
        if(t !=null) {
            synchronized (this) {
                cardsInPlay.add(t);
            }

        }
        return 0;
    }


    private void playCardsOfOneTurn()
    {
        ExecutorService localExecutor = null;

        try
        {
            //localExecutor = Executors.newFixedThreadPool(players.size());
            localExecutor = Executors.newWorkStealingPool();

            List<Callable<Integer>> callables = new ArrayList<>();
            for(Player p:players)
            {
                callables.add(() -> onePlayerPlayCard(p));
            }

            /*
            List<Future<Integer>> futures = localExecutor.invokeAll(callables);
            for (Future f: futures)
            {
                f.get();
            }
            */

            localExecutor.invokeAll(callables)
                    .stream()
                    .map(future -> {
                        try {
                            return future.get();
                        }
                        catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    });
                    //.forEach(System.out::println);


            try
            {
                localExecutor.shutdown();
                localExecutor.awaitTermination(5, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                System.err.println("playCardsOfOneTurn: tasks interrupted: " + e.getMessage());
            }
            finally
            {
                if (!localExecutor.isTerminated())
                {
                    System.err.println("playCardsOfOneTurn: cancel non-finished tasks to shutdown");
                }
                localExecutor.shutdownNow();
            }
        }
        catch (Exception ex)
        {
            System.out.println("playCardsOfOneTurn: exception " + ex.getMessage());
        }
        finally
        {
            if (localExecutor != null)
            {
                //((ThreadPoolExecutor)localExecutor).purge();
            }
        }
    }



    public void playRound()
    {
        //playCardsOfOneTurn();

        //for(Player p:players)

        for(Player p: getPlayers())
        {
            Card t = p.playCard();
            if(t !=null) {
                cardsInPlay.add(t);
                t.setOwner(p);
            }
        }

        turns++;

        // see who won
        Card highest = null;
        for(Card c:cardsInPlay)
        {
            if(highest == null){
                highest = c;
            }else if(highest.getValue() < c.getValue()){
                highest = c;
            }
        }

        // winner gets all played cards
        highest.getOwner().acceptWonCards(cardsInPlay);
        cardsInPlay.clear();
    }


    public Player getWinner()
    {
        if (this.winner != null)
            return this.winner;

        for(Player p:players)
        {
            if(p.has52Cards())
            {
                this.winner = p;
            }
        }

        if (this.winner == null)
        {
            if (this.getTurns() >= ROUNDS_LIMIT)   //put limit so that game doesn't go indefinitely
            {
                //winner will be the one who has the greatest number of cards
                //in case of tie, play one more round to find the winner
                int highest = 0;
                int indexOfHighest = -1;
                for (int i=0; i < this.getPlayers().size(); i++)   //find first highest
                {
                    Player p = this.getPlayers().get(i);
                    int totalCards = p.getWonCards().size() + p.getCardsToPlayWith().size();
                    if (totalCards > highest)
                    {
                        highest = totalCards;
                        indexOfHighest = i;
                    }
                }

                boolean tie = false;
                for (int i=0; i < this.getPlayers().size(); i++)   //search for ties
                {
                    if (i == indexOfHighest)
                        continue;
                    Player p = this.getPlayers().get(i);
                    int totalCards = p.getWonCards().size() + p.getCardsToPlayWith().size();
                    if (totalCards == highest)  //there is a tie
                    {
                        tie = true;
                        break;
                    }
                }

                if (!tie)
                {
                    this.setWinner(this.getPlayers().get(indexOfHighest));
                }
            }
        }

        return this.winner;
    }

    public Player playOneRound()
    {
        Player winner = null;
        playRound();
        winner = getWinner();
        return winner;
    }

    public Player startGame(){
        Player winner = null;
        while(winner == null){
            playRound();
            winner = getWinner();
        }
        return winner;
    }

    public static void shuffle(List<Card> shuffleMePlease){
        // pick random number
        Collections.shuffle(shuffleMePlease);
    }


}
