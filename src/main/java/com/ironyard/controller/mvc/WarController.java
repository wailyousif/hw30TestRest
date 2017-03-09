package com.ironyard.controller.mvc;


import com.ironyard.data.Game;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class WarController
{
    @RequestMapping(value = "/startgame", method = RequestMethod.GET)
    public String startGame(HttpSession session)
    {
        Game aGame = new Game();
        aGame.initGame(2);
        aGame.dealOutAllCards();
        session.setAttribute("game",aGame);
        return "war";
    }

    @RequestMapping(value = "/turn", method = RequestMethod.GET)
    public String turn(HttpSession session) {
        Game aGame = (Game) session.getAttribute("game");
        aGame.playRound();
        session.setAttribute("winner",aGame.getWinner());
        return "war";
    }
}