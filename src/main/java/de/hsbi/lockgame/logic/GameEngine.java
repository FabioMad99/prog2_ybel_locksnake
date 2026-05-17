package de.hsbi.lockgame.logic;

import de.hsbi.lockgame.model.*;

import java.util.ArrayList;
import java.util.List;

public final class GameEngine implements DirectionListener {

    private GameState state;

    private final List<GameStateListener> stateListeners = new ArrayList<>();

    public GameEngine(Level level) {
        this.state = new GameState(
            level,
            new Snake(List.of(level.snakeStart())),
            new ArrayList<>(level.pins()),
            GameState.Status.RUNNING,
            Direction.NONE
        );
    }

    public void addListener(GameStateListener l) {
        stateListeners.add(l);
    }

    public void removeListener(GameStateListener l) {
        stateListeners.remove(l);
    }

    private void notifyStateListeners() {
        for (var l : stateListeners) {
            l.onStateChanged(state);
        }
    }

    @Override
    public void onDirection(Direction direction) {
        state = new GameState(
            state.level(),
            state.snake(),
            state.pins(),
            state.status(),
            direction
        );

        notifyStateListeners();
    }

    public void update(Direction direction) {
        onDirection(direction);
    }

    public void tick() {
        state = state.tick();
        notifyStateListeners();
    }

    public GameState state() {
        return state;
    }
}
