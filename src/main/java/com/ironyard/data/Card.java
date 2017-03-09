package com.ironyard.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jasonskipper on 10/20/16.
 */


public class Card {

    public enum SUIT {
        HEARTS,
        SPADES,
        DIAMONDS,
        CLUBS
    }

    public Card() { }

    private int value;
    private String name;

    //@JsonBackReference

    @JsonIgnore
    private Player owner;

    private SUIT suit;
    private String url;

    public Card(int value, String name, Player owner, SUIT suit) {
        this.value = value;
        this.name = name;
        this.owner = owner;
        this.suit = suit;
    }

    public Card(int value, String name, Player owner, SUIT suit, String url) {
        this.value = value;
        this.name = name;
        this.owner = owner;
        this.suit = suit;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static List<Card> createDeck(){
        List<Card> deck = new ArrayList<>();
        for (SUIT aSuit : SUIT.values()) {
            // cards 2 - 10
            for (int i = 2; i <= 10; i++) {
                String aCardName = String.format("%s of %s", i, aSuit);
                deck.add(new Card(i, aCardName, null, aSuit));
            }
            // face cards
            deck.add(new Card(11, String.format("Ace of %s", aSuit), null, aSuit));
            deck.add(new Card(10, String.format("King of %s", aSuit), null, aSuit));
            deck.add(new Card(10, String.format("Queen of %s", aSuit), null, aSuit));
            deck.add(new Card(10, String.format("Jack of %s", aSuit), null, aSuit));
        }
        return deck;
    }

    public SUIT getSuit() {
        return suit;
    }

    public void setSuit(SUIT suit) {
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }


}
