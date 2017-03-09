package com.ironyard.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonskipper on 10/20/16.
 */

public class Player
{
    //@JsonManagedReference

    private List<Card> cardsToPlayWith;

    //@JsonManagedReference

    private List<Card> wonCards;

    private String name;

    public List<Card> getCardsToPlayWith() {
        return cardsToPlayWith;
    }

    public void setCardsToPlayWith(List<Card> cardsToPlayWith) {
        this.cardsToPlayWith = cardsToPlayWith;
    }

    public List<Card> getWonCards() {
        return wonCards;
    }

    public void setWonCards(List<Card> wonCards) {
        this.wonCards = wonCards;
    }

    public Player()
    {
        this.name = "";
        cardsToPlayWith = new ArrayList<>();
        wonCards = new ArrayList<>();
    }

    public Player(String name) {
        this.name = name;
        cardsToPlayWith = new ArrayList<>();
        wonCards = new ArrayList<>();
    }

    public Player(List<Card> cardsToPlayWith, List<Card> wonCards, String name) {
        this.cardsToPlayWith = cardsToPlayWith;
        this.wonCards = wonCards;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void acceptCardForPlay(Card c){
        c.setOwner(this);
        cardsToPlayWith.add(c);

    }

    public void acceptWonCards(List<Card> hurraa){
        wonCards.addAll(hurraa);
        for(Card c:wonCards){
            c.setOwner(this);
        }
    }

    public Card playCard() {
        Card cardToPlay = null;

        // get card from cardToPlayWith from top
        //if (cardsToPlayWith != null)
        //{
            if (!cardsToPlayWith.isEmpty())
            {
                cardToPlay = cardsToPlayWith.remove(0);
            }
        //}

        // if no cards in carsToPlayWith
        if(cardToPlay == null && !wonCards.isEmpty()) {

                // shuffle won cards
                Game.shuffle(wonCards);

                // move them to cardsToPlayWin
                cardsToPlayWith.addAll(wonCards);
                wonCards.clear();

                // ask yourself fot a card
                // recursive call
                return this.playCard();

        }
        return cardToPlay;
    }

    public boolean has52Cards()
    {
        //System.out.println("cardsToPlayWith.size()=" + cardsToPlayWith.size());
        //System.out.println("wonCards.size()=" + wonCards.size());

        return cardsToPlayWith.size() + wonCards.size() == 52;
    }
}
