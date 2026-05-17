package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import java.util.List;

public final class GameState {

    private Level level;
    private Snake snake;
    private List<Pin> pins;
    private Status status;
    private Direction pendingDirection;

    public GameState(
        Level level, Snake snake, List<Pin> pins, Status status, Direction pendingDirection) {
        this.level = level;
        this.snake = snake;
        this.pins = pins;
        this.status = status;
        this.pendingDirection = pendingDirection;
        // TODO: lege einen neuen GameState mit den übergebenen Informationen an
        //throw new UnsupportedOperationException("method not implemented yet");
    }

    public Level level() {
        return level;
        // TODO: Getter
        //throw new UnsupportedOperationException("method not implemented yet");
    }

    public Snake snake() {
        return snake;
        // TODO: Getter
        //throw new UnsupportedOperationException("method not implemented yet");
    }

    public List<Pin> pins() {
        // TODO: Getter
        return pins;
        //throw new UnsupportedOperationException("method not implemented yet");
    }

    public Status status() {
        // TODO: Getter
        return status;
        //throw new UnsupportedOperationException("method not implemented yet");
    }

    public Direction pendingDirection() {
        // TODO: Getter
        return pendingDirection;
        //throw new UnsupportedOperationException("method not implemented yet");
    }




    public GameState tick() {

        // TODO: diese Methode lässt das Spiel einen Schritt laufen (berechnet den Spielzustand im
        // nächsten Schritt)

        // TODO: early exit: wenn das Spiel nicht läuft oder keine Blickrichtung gesetzt ist: keine
        // Änderung

        // TODO: prüfe die folgenden Bedingungen:
        // (a) Schlange würde das Spielfeld verlassen: Spiel verloren
        // (b) Schlange würde in ein Wandelement gehen: Blockiert (keine Bewegung, Blickrichtung "none")
        // (c) Schlange beisst sich: Spiel verloren
        // (d) Schlange würde auf einen Pin gehen (Pin bereits gesetzt oder Schlange kommt nicht in der
        // Aktivierungsrichtung): Blockiert (keine Bewegung, Blickrichtung "none")

        // TODO: aktiviere einen noch nicht gesetzten Pin, wenn die Schlange in der richtigen Richtung
        // auf den Pin gehen würde (die Schlange darf dabei aber nicht auf den Pin gehen)

        // TODO: anderenfalls: bewege die Schlange um einen Schritt in Blickrichtung (falls gesetzt)
        // early exit

        if (!status.isRunning() || pendingDirection == Direction.NONE) {
            return this;
        }

        Position nextPos = snake.nextHead(pendingDirection);

        // (a) Von der Map (habe ich prob falsch lol)
        if (!level.isInside(nextPos)) {
            return new GameState(
                level,
                snake,
                pins,
                Status.LOST_OUT_OF_BOUNDS,
                Direction.NONE
            );
        }

        // (b) Wand Kollision
        if (level.cellAt(nextPos) == CellType.WALL) {
            return new GameState(
                level,
                snake,
                pins,
                status,
                Direction.NONE
            );
        }

        // (c) Kollision
        if (snake.occupies(nextPos)) {
            return new GameState(
                level,
                snake,
                pins,
                Status.LOST_SELF_COLLISION,
                Direction.NONE
            );
        }

        // (d) Pin
        for (int i = 0; i < pins.size(); i++) {
            Pin pin = pins.get(i);

            if (pin.position().equals(nextPos)) {

                // already activated -> blocked
                if (pin.state().isSet()) {
                    return new GameState(
                        level,
                        snake,
                        pins,
                        status,
                        Direction.NONE
                    );
                }

                // Falsche bewegung = Blocked
                if (pendingDirection != pin.activationDirection()) {
                    return new GameState(
                        level,
                        snake,
                        pins,
                        status,
                        Direction.NONE
                    );
                }

                // Ping aktivierung OHNE reinzugehen
                var newPins = new java.util.ArrayList<>(pins);
                newPins.set(i, pin.withState(Pin.State.HIGH));

                boolean allSet = newPins.stream()
                    .allMatch(p -> p.state().isSet());

                return new GameState(
                    level,
                    snake,
                    newPins,
                    allSet ? Status.WON : Status.RUNNING,
                    pendingDirection
                );
            }
        }

        // Normal state / movement
        Snake movedSnake = snake.grow(pendingDirection);

        return new GameState(
            level,
            movedSnake,
            pins,
            status,
            pendingDirection
        );
    }

    public enum Status {
        RUNNING,
        WON,
        LOST_SELF_COLLISION,
        LOST_OUT_OF_BOUNDS;

        public boolean isRunning() {
            return this == RUNNING;
        }
    }
}
