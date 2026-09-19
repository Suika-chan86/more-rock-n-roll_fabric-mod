package com.suikachan86.morerocknroll.client;

import com.suikachan86.morerocknroll.screen.MusicPlayerScreenHandler;
import com.suikachan86.morerocknroll.track.ModTracks;
import com.suikachan86.morerocknroll.track.TrackDefinition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public final class MusicPlayerScreen extends HandledScreen<MusicPlayerScreenHandler> {
    private static final int WIDTH = 260;
    private static final int HEIGHT = 224;
    private static final int PROGRESS_TOP = 44;
    private static final int PROGRESS_LEFT = 10;
    private static final int PROGRESS_WIDTH = WIDTH - 92;
    private static final int PROGRESS_HEIGHT = 7;
    private static final int TRACK_TOP = 64;
    private static final int ROW_HEIGHT = 16;
    private static final int VISIBLE_TRACKS = 6;
    private static final int LIST_HEIGHT = VISIBLE_TRACKS * ROW_HEIGHT;
    private static final int TRACK_NAME_LEFT = 34;
    private static final int TRACK_NAME_WIDTH = WIDTH - TRACK_NAME_LEFT - 52;
    private static final int SCROLLBAR_LEFT = WIDTH - 16;
    private static final int SCROLLBAR_WIDTH = 8;
    private static final int CONTROL_TOP = TRACK_TOP + LIST_HEIGHT + 8;
    private static final int[] CONTROL_LEFT = {8, 78, 164};
    private static final int[] CONTROL_WIDTH = {60, 76, 88};

    private int focusedOption;
    private int scrollOffset;
    private boolean draggingScrollbar;
    private boolean initialScrollApplied;

    public MusicPlayerScreen(
            MusicPlayerScreenHandler handler,
            PlayerInventory inventory,
            Text title
    ) {
        super(handler, inventory, title);
        backgroundWidth = WIDTH;
        backgroundHeight = HEIGHT;
        titleX = 10;
        titleY = 8;
        playerInventoryTitleX = 0;
        playerInventoryTitleY = 0;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xE0101010);
        context.drawBorder(x, y, backgroundWidth, backgroundHeight, 0xFF808080);
        context.drawHorizontalLine(x + 10, x + backgroundWidth - 10, y + TRACK_TOP - 6, 0xFF505050);
        context.drawHorizontalLine(x + 10, x + backgroundWidth - 10, y + CONTROL_TOP - 4, 0xFF505050);
        applyInitialScroll();
        drawProgressBar(context);
        drawScrollbar(context);
        ensureFocusEnabled();
        drawFocus(context);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(textRenderer, title, titleX, titleY, 0xFFFFFF, false);
        context.drawText(
                textRenderer,
                currentTrackText(),
                10,
                20,
                0xFFFFFF,
                false
        );
        context.drawText(
                textRenderer,
                statusText(),
                10,
                32,
                0xB0B0B0,
                false
        );
        context.drawText(
                textRenderer,
                progressText(),
                PROGRESS_LEFT + PROGRESS_WIDTH + 10,
                PROGRESS_TOP - 3,
                0xA0A0A0,
                false
        );
        context.drawText(
                textRenderer,
                Text.translatable("screen.more-rock-n-roll.music_player.select_track"),
                10,
                TRACK_TOP - 12,
                0xB0B0B0,
                false
        );

        for (int visibleRow = 0; visibleRow < VISIBLE_TRACKS; visibleRow++) {
            int trackIndex = scrollOffset + visibleRow;
            if (trackIndex >= ModTracks.ALL.size()) {
                break;
            }

            TrackDefinition track = ModTracks.ALL.get(trackIndex);
            int rowY = TRACK_TOP + visibleRow * ROW_HEIGHT;
            int trackColor = trackIndex == handler.currentTrackIndex() ? 0xFFFFD866 : 0xFFFFFF;
            context.drawText(
                    textRenderer,
                    Text.literal(String.format(Locale.ROOT, "%d.", trackIndex + 1)),
                    12,
                    rowY,
                    0x909090,
                    false
            );
            context.drawText(
                    textRenderer,
                    trackNameText(track),
                    TRACK_NAME_LEFT,
                    rowY,
                    trackColor,
                    false
            );
            context.drawText(
                    textRenderer,
                    durationText(track),
                    backgroundWidth - 42,
                    rowY,
                    0xA0A0A0,
                    false
            );
        }

        context.drawText(
                textRenderer,
                Text.translatable("screen.more-rock-n-roll.music_player.pause"),
                16,
                CONTROL_TOP,
                controlColor(handler.canPause()),
                false
        );
        context.drawText(
                textRenderer,
                Text.translatable("screen.more-rock-n-roll.music_player.resume"),
                86,
                CONTROL_TOP,
                controlColor(handler.canResume()),
                false
        );
        context.drawText(
                textRenderer,
                Text.translatable("screen.more-rock-n-roll.music_player.stop"),
                174,
                CONTROL_TOP,
                controlColor(handler.canStop()),
                false
        );
        context.drawText(
                textRenderer,
                Text.translatable("screen.more-rock-n-roll.music_player.keyboard_hint"),
                10,
                backgroundHeight - 14,
                0xA0A0A0,
                false
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        double localX = mouseX - x;
        double localY = mouseY - y;
        if (isScrollbarAt(localX, localY)) {
            if (localY >= scrollbarThumbTop() && localY < scrollbarThumbTop() + scrollbarThumbHeight()) {
                draggingScrollbar = true;
                updateScrollFromMouse(localY);
            } else {
                scrollOffset += localY < scrollbarThumbTop() ? -VISIBLE_TRACKS : VISIBLE_TRACKS;
                clampScrollOffset();
            }
            return true;
        }

        int trackIndex = trackAt(localX, localY);
        if (trackIndex >= 0) {
            focusedOption = trackIndex;
            ensureTrackVisible(trackIndex);
            activateFocusedOption();
            return true;
        }

        int controlIndex = controlAt(localX, localY);
        if (controlIndex >= 0) {
            focusedOption = ModTracks.ALL.size() + controlIndex;
            activateFocusedOption();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && draggingScrollbar) {
            updateScrollFromMouse(mouseY - y);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingScrollbar = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double localX = mouseX - x;
        double localY = mouseY - y;
        if (isInsideTrackViewport(localX, localY) && maxScrollOffset() > 0) {
            scrollOffset += verticalAmount < 0 ? 1 : -1;
            clampScrollOffset();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_LEFT -> focusPrevious();
            case GLFW.GLFW_KEY_DOWN, GLFW.GLFW_KEY_RIGHT -> focusNext();
            case GLFW.GLFW_KEY_HOME -> {
                focusedOption = firstEnabledOption();
                ensureTrackVisible(focusedOption);
            }
            case GLFW.GLFW_KEY_END -> {
                focusedOption = lastEnabledOption();
                ensureTrackVisible(focusedOption);
            }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER, GLFW.GLFW_KEY_SPACE -> {
                activateFocusedOption();
                return true;
            }
            default -> {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return true;
    }

    private int trackAt(double localX, double localY) {
        if (!isInsideTrackViewport(localX, localY)) {
            return -1;
        }

        int visibleRow = (int) ((localY - TRACK_TOP) / ROW_HEIGHT);
        int trackIndex = scrollOffset + visibleRow;
        return trackIndex < ModTracks.ALL.size() ? trackIndex : -1;
    }

    private boolean isInsideTrackViewport(double localX, double localY) {
        return localX >= 8
                && localX < SCROLLBAR_LEFT
                && localY >= TRACK_TOP
                && localY < TRACK_TOP + LIST_HEIGHT;
    }

    private int controlAt(double localX, double localY) {
        if (localY < CONTROL_TOP - 4 || localY >= CONTROL_TOP + 14) {
            return -1;
        }

        for (int index = 0; index < CONTROL_LEFT.length; index++) {
            if (localX >= CONTROL_LEFT[index]
                    && localX < CONTROL_LEFT[index] + CONTROL_WIDTH[index]) {
                return index;
            }
        }
        return -1;
    }

    private void drawProgressBar(DrawContext context) {
        context.fill(
                x + PROGRESS_LEFT,
                y + PROGRESS_TOP,
                x + PROGRESS_LEFT + PROGRESS_WIDTH,
                y + PROGRESS_TOP + PROGRESS_HEIGHT,
                0xFF202020
        );
        TrackDefinition track = currentTrack();
        if (track != null) {
            float progress = Math.min(1.0f, handler.positionTicks() / (float) track.lengthTicks());
            int filledWidth = Math.round(PROGRESS_WIDTH * progress);
            context.fill(
                    x + PROGRESS_LEFT,
                    y + PROGRESS_TOP,
                    x + PROGRESS_LEFT + filledWidth,
                    y + PROGRESS_TOP + PROGRESS_HEIGHT,
                    0xFF6FA8DC
            );
        }
        context.drawBorder(
                x + PROGRESS_LEFT,
                y + PROGRESS_TOP,
                PROGRESS_WIDTH,
                PROGRESS_HEIGHT,
                0xFF808080
        );
    }

    private void drawScrollbar(DrawContext context) {
        if (maxScrollOffset() <= 0) {
            return;
        }

        context.fill(
                x + SCROLLBAR_LEFT,
                y + TRACK_TOP,
                x + SCROLLBAR_LEFT + SCROLLBAR_WIDTH,
                y + TRACK_TOP + LIST_HEIGHT,
                0xFF202020
        );
        context.fill(
                x + SCROLLBAR_LEFT,
                y + scrollbarThumbTop(),
                x + SCROLLBAR_LEFT + SCROLLBAR_WIDTH,
                y + scrollbarThumbTop() + scrollbarThumbHeight(),
                draggingScrollbar ? 0xFFB0B0B0 : 0xFF707070
        );
        context.drawBorder(
                x + SCROLLBAR_LEFT,
                y + scrollbarThumbTop(),
                SCROLLBAR_WIDTH,
                scrollbarThumbHeight(),
                0xFFB0B0B0
        );
    }

    private boolean isScrollbarAt(double localX, double localY) {
        return maxScrollOffset() > 0
                && localX >= SCROLLBAR_LEFT
                && localX < SCROLLBAR_LEFT + SCROLLBAR_WIDTH
                && localY >= TRACK_TOP
                && localY < TRACK_TOP + LIST_HEIGHT;
    }

    private int scrollbarThumbHeight() {
        return Math.max(12, LIST_HEIGHT * VISIBLE_TRACKS / ModTracks.ALL.size());
    }

    private int scrollbarThumbTop() {
        int travel = LIST_HEIGHT - scrollbarThumbHeight();
        if (travel <= 0 || maxScrollOffset() == 0) {
            return TRACK_TOP;
        }
        return TRACK_TOP + Math.round(travel * scrollOffset / (float) maxScrollOffset());
    }

    private void updateScrollFromMouse(double localY) {
        int travel = LIST_HEIGHT - scrollbarThumbHeight();
        if (travel <= 0) {
            scrollOffset = 0;
            return;
        }

        double ratio = (localY - TRACK_TOP - scrollbarThumbHeight() / 2.0) / travel;
        scrollOffset = (int) Math.round(Math.max(0.0, Math.min(1.0, ratio)) * maxScrollOffset());
    }


    private void ensureTrackVisible(int trackIndex) {
        if (trackIndex < 0 || trackIndex >= ModTracks.ALL.size()) {
            return;
        }
        if (trackIndex < scrollOffset) {
            scrollOffset = trackIndex;
        } else if (trackIndex >= scrollOffset + VISIBLE_TRACKS) {
            scrollOffset = trackIndex - VISIBLE_TRACKS + 1;
        }
        clampScrollOffset();
    }

    private void applyInitialScroll() {
        if (initialScrollApplied) {
            return;
        }

        int currentTrackIndex = handler.currentTrackIndex();
        if (currentTrackIndex >= 0) {
            focusedOption = currentTrackIndex;
            ensureTrackVisible(currentTrackIndex);
            initialScrollApplied = true;
        }
    }

    private void clampScrollOffset() {
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScrollOffset()));
    }

    private int maxScrollOffset() {
        return Math.max(0, ModTracks.ALL.size() - VISIBLE_TRACKS);
    }

    private void drawFocus(DrawContext context) {
        if (focusedOption < ModTracks.ALL.size()) {
            int visibleRow = focusedOption - scrollOffset;
            if (visibleRow < 0 || visibleRow >= VISIBLE_TRACKS) {
                return;
            }
            int top = TRACK_TOP + visibleRow * ROW_HEIGHT - 2;
            drawFocusBox(context, 8, top, SCROLLBAR_LEFT - 8, ROW_HEIGHT);
            return;
        }

        int controlIndex = focusedOption - ModTracks.ALL.size();
        drawFocusBox(
                context,
                CONTROL_LEFT[controlIndex],
                CONTROL_TOP - 4,
                CONTROL_WIDTH[controlIndex],
                18
        );
    }

    private void drawFocusBox(DrawContext context, int left, int top, int width, int height) {
        context.fill(x + left, y + top, x + left + width, y + top + height, 0x553F75A5);
        context.drawBorder(x + left, y + top, width, height, 0xFF6FA8DC);
    }

    private void focusPrevious() {
        moveFocus(-1);
    }

    private void focusNext() {
        moveFocus(1);
    }

    private void moveFocus(int direction) {
        int candidate = focusedOption;
        for (int attempt = 0; attempt < optionCount(); attempt++) {
            candidate = Math.floorMod(candidate + direction, optionCount());
            if (isOptionEnabled(candidate)) {
                focusedOption = candidate;
                ensureTrackVisible(candidate);
                return;
            }
        }
    }

    private int firstEnabledOption() {
        for (int option = 0; option < optionCount(); option++) {
            if (isOptionEnabled(option)) {
                return option;
            }
        }
        return 0;
    }

    private int lastEnabledOption() {
        for (int option = optionCount() - 1; option >= 0; option--) {
            if (isOptionEnabled(option)) {
                return option;
            }
        }
        return 0;
    }

    private void ensureFocusEnabled() {
        if (!isOptionEnabled(focusedOption)) {
            focusedOption = firstEnabledOption();
        }
    }

    private boolean isOptionEnabled(int option) {
        if (option < ModTracks.ALL.size()) {
            return true;
        }

        return switch (option - ModTracks.ALL.size()) {
            case 0 -> handler.canPause();
            case 1 -> handler.canResume();
            case 2 -> handler.canStop();
            default -> false;
        };
    }

    private int optionCount() {
        return ModTracks.ALL.size() + CONTROL_LEFT.length;
    }

    private void activateFocusedOption() {
        if (!isOptionEnabled(focusedOption)) {
            return;
        }

        if (focusedOption < ModTracks.ALL.size()) {
            clickButton(focusedOption);
            return;
        }

        int controlIndex = focusedOption - ModTracks.ALL.size();
        clickButton(switch (controlIndex) {
            case 0 -> MusicPlayerScreenHandler.PAUSE_BUTTON;
            case 1 -> MusicPlayerScreenHandler.RESUME_BUTTON;
            case 2 -> MusicPlayerScreenHandler.STOP_BUTTON;
            default -> throw new IllegalStateException("Unknown music player control: " + controlIndex);
        });
    }

    private void clickButton(int id) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.interactionManager != null) {
            client.interactionManager.clickButton(handler.syncId, id);
        }
    }

    private TrackDefinition currentTrack() {
        int index = handler.currentTrackIndex();
        if (index < 0 || index >= ModTracks.ALL.size()) {
            return null;
        }
        return ModTracks.ALL.get(index);
    }

    private Text currentTrackText() {
        TrackDefinition track = currentTrack();
        if (track == null) {
            return Text.translatable("screen.more-rock-n-roll.music_player.no_track");
        }
        return Text.literal(
                textRenderer.trimToWidth(
                        Text.translatable(
                                "screen.more-rock-n-roll.music_player.current_track",
                                Text.translatable(track.jukeboxSongTranslationKey())
                        ),
                        backgroundWidth - 20
                ).getString()
        );
    }

    private Text trackNameText(TrackDefinition track) {
        return Text.literal(
                textRenderer.trimToWidth(
                        Text.translatable(track.jukeboxSongTranslationKey()),
                        TRACK_NAME_WIDTH
                ).getString()
        );
    }

    private Text statusText() {
        String key = switch (handler.playbackState()) {
            case PLAYING -> "screen.more-rock-n-roll.music_player.status.playing";
            case PAUSED -> "screen.more-rock-n-roll.music_player.status.paused";
            case STOPPED -> "screen.more-rock-n-roll.music_player.status.stopped";
        };
        return Text.translatable(
                "screen.more-rock-n-roll.music_player.status",
                Text.translatable(key)
        );
    }

    private Text progressText() {
        TrackDefinition track = currentTrack();
        if (track == null) {
            return Text.literal("-:-- / -:--");
        }

        long positionTicks = Math.min((long) handler.positionTicks(), track.lengthTicks());
        return Text.literal(formatTime(positionTicks) + " / " + formatTime(track.lengthTicks()));
    }

    private int controlColor(boolean enabled) {
        return enabled ? 0xFFFFFF : 0x606060;
    }

    private static Text durationText(TrackDefinition track) {
        return Text.literal(formatTime(track.lengthTicks()));
    }

    private static String formatTime(long ticks) {
        long totalSeconds = Math.max(0L, ticks) / 20L;
        return String.format(Locale.ROOT, "%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}