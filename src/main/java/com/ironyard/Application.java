package com.ironyard;

import com.ironyard.data.Game;
import com.ironyard.data.Player;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@SpringBootApplication
@ComponentScan(basePackages = "com.ironyard")
public class Application
{
    private static final int NUMBER_OF_GAMES = 5;
    private int gameNumber;
    private int gamesEnded;


    private String doGame(Game aGame, int numOfPlayers)
    {
        int localGameNumber;
        synchronized (this) {
            gameNumber++;
            localGameNumber = gameNumber;
        }

        System.out.println("Starting game #" + localGameNumber);

        aGame.initGame(numOfPlayers);
        aGame.dealOutAllCards();

        Player winner = null;
        while (winner == null)
        {
            aGame.playRound();
            winner = aGame.getWinner();
        }

        synchronized (this) {
            gamesEnded++;
        }
        String declareWinner = "Winner of game#" + String.valueOf(localGameNumber) + " is: " + winner.getName() +
                "  (Games started = " + gameNumber + ", Games ended = " + gamesEnded + ").";
        System.out.println(declareWinner);
        return declareWinner;
    }


    private void doGameThreads_Method1() throws ExecutionException, InterruptedException
    {
        System.out.println("STARTING ALL GAMES\n-----------------------");

        ExecutorService executor = Executors.newWorkStealingPool();
        gameNumber = 0;
        gamesEnded = 0;

        List<Callable<String>> callables = new ArrayList<>();
        for (int i=0; i < NUMBER_OF_GAMES; i++)
        {
            callables.add(() -> doGame(new Game(), 5));
        }

        executor.invokeAll(callables)
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
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            System.err.println("doGameThreads_Method1: tasks interrupted " + e.getMessage());
        }
        finally
        {
            if (!executor.isTerminated()) {
                System.err.println("doGameThreads_Method1: cancel non-finished tasks to shutdown");
            }
            executor.shutdownNow();

            System.out.println("ALL GAMES ENDED!\n-----------------------");
        }
    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        SpringApplication.run(Application.class, args);

        (new Application()).doGameThreads_Method1();
    }


    @Bean
    public Docket chatApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("play-cards")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/rest/cards/*.*"))
                .build();
				/*
				.globalOperationParameters(
						newArrayList(new ParameterBuilder()
								.name("x-authorization-key")
								.description("API Authorization Key")
								.modelRef(new ModelRef("string"))
								.parameterType("header")
								.required(true)
								.build()));*/

    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Play Cards APIs")
                .description("PlayCards")
                .termsOfServiceUrl("http://www.theironyard.com")
                .contact("Wail Yousif")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
                .version("2.1")
                .build();
    }

}