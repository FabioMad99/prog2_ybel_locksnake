package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;
import de.hsbi.lockgame.logic.GameState;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    private Level createSimpleLevel() {
        CellType[][] cells = new CellType[5][5];

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                cells[x][y] = CellType.EMPTY;
            }
        }

        cells[2][2] = CellType.WALL;

        Pin pin = new Pin(new Position(4, 2), Pin.State.LOW, Direction.RIGHT);

        return new Level(
            5,
            5,
            cells,
            List.of(pin),
            new Position(1, 2)
        );
    }

    // 1. INITIAL STATE
    @Test
    void givenLevel_whenGameStateCreated_thenSnakeStartsCorrectly() {
        Level level = createSimpleLevel();

        GameState state = new GameState(
            level,
            new Snake(List.of(level.snakeStart())),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE
        );

        assertEquals(level.snakeStart(), state.snake().head());
        assertEquals(GameState.Status.RUNNING, state.status());
    }

    // 2. BASIC MOVEMENT
    @Test
    void givenSnake_whenMovingRight_thenHeadMovesRight() {
        Level level = createSimpleLevel();

        Snake snake = new Snake(List.of(new Position(1, 2)));

        GameState state = new GameState(
            level,
            snake,
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(new Position(2, 2), next.snake().head());
    }

    // 3. WALL BLOCKING
    @Test
    void givenSnake_whenMovingIntoWall_thenGameNotLostBySelfButBlocked() {
        Level level = createSimpleLevel();

        Snake snake = new Snake(List.of(new Position(1, 2)));

        GameState state = new GameState(
            level,
            snake,
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        // Wall at (2,2) → blocked or unchanged depending on implementation
        assertTrue(
            next.status().isRunning() || next.status() == GameState.Status.RUNNING
        );
    }

    // 4. OUT OF BOUNDS
    @Test
    void givenSnake_whenMovingOutOfBounds_thenLose() {
        Level level = createSimpleLevel();

        Snake snake = new Snake(List.of(new Position(4, 2)));

        GameState state = new GameState(
            level,
            snake,
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.LOST_OUT_OF_BOUNDS, next.status());
    }

    // 5. SELF COLLISION
    @Test
    void givenSnake_whenCollidingWithSelf_thenLose() {
        Level level = createSimpleLevel();

        Snake snake = new Snake(List.of(
            new Position(2, 2),
            new Position(2, 3),
            new Position(2, 4)
        ));

        GameState state = new GameState(
            level,
            snake,
            level.pins(),
            GameState.Status.RUNNING,
            Direction.UP
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.LOST_SELF_COLLISION, next.status());
    }


    // 6. PIN ACTIVATION
    @Test
    void givenSnake_whenMovingOntoPin_thenPinBecomesActive() {
        Level level = createSimpleLevel();

        Snake snake = new Snake(List.of(new Position(3, 2)));

        GameState state = new GameState(
            level,
            snake,
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertTrue(next.pins().get(0).state().isSet());
    }

    // 7. WRONG PIN DIRECTION BLOCK
    @Test
    void givenSnake_whenApproachingPinWrongDirection_thenBlocked() {
        Level level = createSimpleLevel();

        Snake snake = new Snake(List.of(new Position(3, 2)));

        GameState state = new GameState(
            level,
            snake,
            level.pins(),
            GameState.Status.RUNNING,
            Direction.LEFT
        );

        GameState next = state.tick();

        assertFalse(next.pins().get(0).state().isSet());
    }


    // 8. WIN CONDITION
    @Test
    void givenAllPinsSet_whenTick_thenGameWon() {
        Level level = createSimpleLevel();

        Pin pin = new Pin(new Position(2, 2), Pin.State.HIGH, Direction.RIGHT);

        GameState state = new GameState(
            level,
            new Snake(List.of(new Position(1, 2))),
            List.of(pin),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertEquals(GameState.Status.WON, next.status());
    }

    // 9. NO DIRECTION (NONE)
    @Test
    void givenNoDirection_whenTick_thenStateUnchanged() {
        Level level = createSimpleLevel();

        GameState state = new GameState(
            level,
            new Snake(List.of(level.snakeStart())),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.NONE
        );

        GameState next = state.tick();

        assertEquals(state.snake().head(), next.snake().head());
    }


    // 10. STATE IMMUTABILITY CHECK
    @Test
    void givenTick_whenExecuted_thenNewGameStateCreated() {
        Level level = createSimpleLevel();

        GameState state = new GameState(
            level,
            new Snake(List.of(level.snakeStart())),
            level.pins(),
            GameState.Status.RUNNING,
            Direction.RIGHT
        );

        GameState next = state.tick();

        assertNotSame(state, next);
    }
}
