package com.udacity.vinhphuc.musicplayer.data.model;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by VINH PHUC on 12/8/2018
 */
public class Lyrics {
    private static final ArrayList<Class<? extends Lyrics>> FORMATS = new ArrayList<>();

    public Song song;
    public String data;

    protected boolean parsed = false;
    protected boolean valid = false;

    public Lyrics setData(Song song, String data) {
        this.song = song;
        this.data = data;
        return this;
    }

    public static Lyrics parse(Song song, String data) {
        for (Class<? extends Lyrics> format : Lyrics.FORMATS) {
            try {
                Lyrics lyrics = format.newInstance().setData(song, data);
                if (lyrics.isValid()) return lyrics.parse(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Lyrics().setData(song, data).parse(false);
    }

    public static boolean isSynchronized(String data) {
        for (Class<? extends Lyrics> format : Lyrics.FORMATS) {
            try {
                Lyrics lyrics = format.newInstance().setData(null, data);
                if (lyrics.isValid()) return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Lyrics parse(boolean check) {
        this.valid = true;
        this.parsed = true;
        return this;
    }

    public boolean isSynchronized() {
        return false;
    }

    public boolean isValid() {
        this.parse(true);
        return this.valid;
    }

    public String getText() {
        return this.data.trim().replaceAll("(\r?\n){3,}", "\r\n\r\n");
    }

    static {
        Lyrics.FORMATS.add(SynchronizedLyricsLRC.class);
    }

    public abstract class AbsSynchronizedLyrics extends Lyrics {
        private static final int TIME_OFFSET_MS = 500; // time adjustment to display line before it actually starts

        protected final SparseArray<String> lines = new SparseArray<>();
        protected int offset = 0;

        public String getLine(int time) {
            time += offset + AbsSynchronizedLyrics.TIME_OFFSET_MS;

            int lastLineTime = lines.keyAt(0);

            for (int i = 0; i < lines.size(); i++) {
                int lineTime = lines.keyAt(i);

                if (time >= lineTime) {
                    lastLineTime = lineTime;
                } else {
                    break;
                }
            }

            return lines.get(lastLineTime);
        }

        public boolean isSynchronized() {
            return true;
        }

        public boolean isValid() {
            parse(true);
            return valid;
        }

        @Override
        public String getText() {
            parse(false);

            if (valid) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.valueAt(i);
                    sb.append(line).append("\r\n");
                }

                return sb.toString().trim().replaceAll("(\r?\n){3,}", "\r\n\r\n");
            }

            return super.getText();
        }
    }

    class SynchronizedLyricsLRC extends AbsSynchronizedLyrics {
        private final Pattern LRC_LINE_PATTERN = Pattern.compile("((?:\\[.*?\\])+)(.*)");
        private final Pattern LRC_TIME_PATTERN = Pattern.compile("\\[(\\d+):(\\d{2}(?:\\.\\d+)?)\\]");
        private final Pattern LRC_ATTRIBUTE_PATTERN = Pattern.compile("\\[(\\D+):(.+)\\]");

        private static final float LRC_SECONDS_TO_MS_MULTIPLIER = 1000f;
        private static final int LRC_MINUTES_TO_MS_MULTIPLIER = 60000;

        @Override
        public SynchronizedLyricsLRC parse(boolean check) {
            if (this.parsed || this.data == null || this.data.isEmpty()) {
                return this;
            }

            String[] lines = this.data.split("\r?\n");

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                Matcher attrMatcher = LRC_ATTRIBUTE_PATTERN.matcher(line);
                if (attrMatcher.find()) {
                    try {
                        String attr = attrMatcher.group(1).toLowerCase().trim();
                        String value = attrMatcher.group(2).toLowerCase().trim();
                        switch (attr) {
                            case "offset":
                                this.offset = Integer.parseInt(value);
                                break;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Matcher matcher = LRC_LINE_PATTERN.matcher(line);
                    if (matcher.find()) {
                        String time = matcher.group(1);
                        String text = matcher.group(2);

                        Matcher timeMatcher = LRC_TIME_PATTERN.matcher(time);
                        while (timeMatcher.find()) {
                            int m = 0;
                            float s = 0f;
                            try {
                                m = Integer.parseInt(timeMatcher.group(1));
                                s = Float.parseFloat(timeMatcher.group(2));
                            } catch (NumberFormatException ex) {
                                ex.printStackTrace();
                            }
                            int ms = (int) (s * LRC_SECONDS_TO_MS_MULTIPLIER) + m * LRC_MINUTES_TO_MS_MULTIPLIER;

                            this.valid = true;
                            if (check) return this;

                            this.lines.append(ms, text);
                        }
                    }
                }
            }

            this.parsed = true;

            return this;
        }
    }
}
