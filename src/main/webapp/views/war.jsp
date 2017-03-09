

<html>
<title>This is WAR!</title>
<body>
<h2>Will you play a game?</h2>
play new game: <a href="/startgame">Start New Game</a>

play new game: <a href="/turn">Play a round</a>
<c:out value="${game}"/>

<c:forEach var="person" items="${game.players}">
    <c:out value="${person.name}"/>
</c:forEach>
</body>
</html>