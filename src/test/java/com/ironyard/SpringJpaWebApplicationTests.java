package com.ironyard;


import com.google.gson.Gson;
import com.ironyard.data.Game;
import com.ironyard.data.Player;
import com.ironyard.dto.ResponseObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class SpringJpaWebApplicationTests
{
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters)
    {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);
        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }


    protected String json(Object o) throws IOException
    {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }


    @Test
    public void contextLoads() { }


    @Before
    public void setUp() throws Exception
    {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }


    private ResponseObject startGame(Integer numberOfPlayers, Integer expectedResponseCode) throws Exception
    {
        /*
        MvcResult result = this.mockMvc.perform(get("/rest/cards/startgame/" + numberOfPlayers.toString())
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.responseCode", is( expectedResponseCode )))
                .andReturn();
                */

        MvcResult result = this.mockMvc.perform(get("/rest/cards/startgame")
                .param("numberOfPlayers", numberOfPlayers.toString())
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.responseCode", is( expectedResponseCode )))
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();
        System.out.println("startGame() response jsonString=" + jsonString + "#");
        ResponseObject responseObject = (new Gson()).fromJson(jsonString, ResponseObject.class);

        return responseObject;
    }


    @Test
    public void test1_StartGame_With_PositiveNumberOfPlayers() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseObject responseObject = startGame(5, 0);
        System.out.println("User's Message: " + responseObject.getResponseString());
    }


    @Test
    public void test2_StartGame_With_OnePlayers() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseObject responseObject = startGame(1, -10);
        System.out.println("User's Message: " + responseObject.getResponseString());
    }


    @Test
    public void test3_StartGame_With_ZeroNumberOfPlayers() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseObject responseObject = startGame(0, -10);
        System.out.println("User's Message: " + responseObject.getResponseString());
    }


    @Test
    public void test4_StartGame_With_NegativeNumberOfPlayers() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseObject responseObject = startGame(-3, -10);
        System.out.println("User's Message: " + responseObject.getResponseString());
    }


    @Test
    public void test5_StartGame_With_NullNumberOfPlayers() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());

        MvcResult result = this.mockMvc.perform(get("/rest/cards/startgame")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.responseCode", is( -20 )))
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();
        System.out.println("startGame() response jsonString=" + jsonString + "#");
        ResponseObject responseObject = (new Gson()).fromJson(jsonString, ResponseObject.class);

        System.out.println("User's Message: " + responseObject.getResponseString());
    }


    private ResponseObject playOneRound(Game aGame, Integer expectedResponseCode, boolean debug) throws Exception
    {
        MvcResult result = this.mockMvc.perform(post("/rest/cards/turn")
                .content(this.json(aGame))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.responseCode", is( expectedResponseCode )))
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();
        if (debug)
            System.out.println("playOneRound() response jsonString=" + jsonString + "#");
        ResponseObject responseObject = (new Gson()).fromJson(jsonString, ResponseObject.class);

        return responseObject;
    }


    @Test
    public void test6_PlayOneRound() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());
        ResponseObject responseObject1 = startGame(4, 0);
        ResponseObject responseObject2 = playOneRound(responseObject1.getGame(), 0, true);
        System.out.println("User's Message: " + responseObject2.getResponseString());
    }


    @Test
    public void test7_SimulateFullGameAtServer() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());

        Integer numberOfPlayers = 4;
        MvcResult result = this.mockMvc.perform(get("/rest/cards/simulatefullgame")
                .param("numberOfPlayers", numberOfPlayers.toString())
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.responseCode", is( 0 )))
                .andReturn();

        String jsonString = result.getResponse().getContentAsString();
        System.out.println("simulateFullGame() response jsonString=" + jsonString + "#");
        ResponseObject responseObject = (new Gson()).fromJson(jsonString, ResponseObject.class);
        System.out.println("User's Message: " + responseObject.getResponseString());
    }


    @Test
    public void test8_SimulateFullGame() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());
        Integer numberOfPlayers = 4;
        ResponseObject respObj = startGame(numberOfPlayers, 0);

        Player winner = null;
        Integer rounds = 0;
        while (winner == null)
        {
            respObj = playOneRound(respObj.getGame(), 0, false);
            rounds++;
            if (rounds % 10 == 0)  //give update
            {
                System.out.println("json:" + this.json(respObj));
                for (int i=0; i < numberOfPlayers; i++)
                {
                    Player player = respObj.getGame().getPlayers().get(i);
                    System.out.println("\t" + player.getName() + ": Won(" + player.getWonCards().size() +
                            ")  PlayWith(" + player.getCardsToPlayWith().size() + ")");
                }
            }

            winner = respObj.getGame().getWinner();
        }

        System.out.println("final json:" + this.json(respObj));
        System.out.println("User's Message: " + respObj.getResponseString());
    }


    @Test
    public void test9_PlayRoundAfterGameOver() throws Exception
    {
        System.out.println("Test: " + Thread.currentThread().getStackTrace()[1].getMethodName());
        Integer numberOfPlayers = 4;
        ResponseObject respObj = startGame(numberOfPlayers, 0);

        Player winner = null;
        Integer rounds = 0;
        while (winner == null)
        {
            respObj = playOneRound(respObj.getGame(), 0, false);
            rounds++;
            if (rounds % 10 == 0)  //give update
            {
                System.out.println("json:" + this.json(respObj));
                for (int i=0; i < numberOfPlayers; i++)
                {
                    Player player = respObj.getGame().getPlayers().get(i);
                    System.out.println("\t" + player.getName() + ": Won(" + player.getWonCards().size() +
                            ")  PlayWith(" + player.getCardsToPlayWith().size() + ")");
                }
            }
            winner = respObj.getGame().getWinner();
        }
        respObj = playOneRound(respObj.getGame(), -110, true);

        System.out.println("final json:" + this.json(respObj));
        System.out.println("User's Message: " + respObj.getResponseString());
    }
}
