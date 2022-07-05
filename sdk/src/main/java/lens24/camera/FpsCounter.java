/*
 * Copyright 2011 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */
package lens24.camera;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

final class FpsCounter {

    private int fpsUpdateFramesInterval;
    private long fpsStartTime, fpsLastUpdateTime, fpsLastPeriod, fpsTotalDuration;
    private int fpsTotalFrames;
    private float fpsLast, fpsTotal;
    private float fpsLastTickTime;

    /**
     * Creates a disabled instance
     */
    public FpsCounter() {
        setUpdateFPSFrames(0);
    }

    /**
     * Increases total frame count and updates values if feature is enabled and update interval is
     * reached.<br>
     */
    public synchronized void tickFPS() {
        final long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        if (fpsUpdateFramesInterval == 0) return;

        fpsTotalFrames++;

        if (fpsTotalFrames % fpsUpdateFramesInterval == 0) {
            fpsLastPeriod = now - fpsLastUpdateTime;
            fpsLastPeriod = Math.max(fpsLastPeriod, 1); // div 0
            fpsLast = (fpsUpdateFramesInterval * 1000f) / (fpsLastPeriod ) ;

            fpsTotalDuration = now - fpsStartTime;
            fpsTotalDuration = Math.max(fpsTotalDuration, 1); // div 0
            fpsTotal= ( fpsTotalFrames * 1000f ) / ( fpsTotalDuration ) ;

            fpsLastUpdateTime = now;
        }

        fpsLastTickTime = now;
    }

    public synchronized void update() {
        final long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        if (now - fpsLastTickTime > 1000) {
            fpsLast = 0;
            fpsLastPeriod = 0;
            fpsTotalDuration = now - fpsStartTime;
            fpsTotalDuration = Math.max(fpsTotalDuration, 1); // div 0
            fpsLastUpdateTime = now;
        }
    }

    public StringBuilder toString(StringBuilder sb) {
        if(null==sb) {
            sb = new StringBuilder();
        }
        String fpsLastS = String.valueOf(fpsLast);
        fpsLastS = fpsLastS.substring(0, fpsLastS.indexOf('.') + 2);
        String fpsTotalS = String.valueOf(fpsTotal);
        fpsTotalS = fpsTotalS.substring(0, fpsTotalS.indexOf('.') + 2);
        /* + "total: "+ fpsTotalFrames+" f, "+ fpsTotalS+ " fps, "+ fpsTotalDuration/fpsTotalFrames+" ms/f" */
        sb
                .append(fpsTotalDuration / 1000).append(" s: ")
                .append(fpsUpdateFramesInterval)
                .append(" f / ")
                .append(fpsLastPeriod)
                .append(" ms, ")
                .append(fpsLastS)
                .append(" fps, ")
                .append(fpsLastPeriod / fpsUpdateFramesInterval)
                .append(" ms/f; ");

        return sb;
    }

    @NonNull
    @Override
    public String toString() {
        return toString(null).toString();
    }

    public synchronized void setUpdateFPSFrames(final int frames) {
        fpsUpdateFramesInterval = frames;
        resetFPSCounter();
    }

    public synchronized void resetFPSCounter() {
        fpsStartTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()); // overwrite startTime to real init one
        fpsLastUpdateTime = fpsStartTime;
        fpsTotalFrames = 0;
        fpsLast = 0f;
        fpsTotal = 0f;
        fpsLastPeriod = 0;
        fpsTotalDuration = 0;
    }

    public synchronized int getUpdateFPSFrames() {
        return fpsUpdateFramesInterval;
    }

    public synchronized long getFPSStartTime() {
        return fpsStartTime;
    }

    public synchronized long getLastFPSUpdateTime() {
        return fpsLastUpdateTime;
    }

    public synchronized long getLastFPSPeriod() {
        return fpsLastPeriod;
    }

    public synchronized float getLastFPS() {
        return fpsLast;
    }

    public synchronized int getTotalFPSFrames() {
        return fpsTotalFrames;
    }

    public synchronized long getTotalFPSDuration() {
        return fpsTotalDuration;
    }

    public synchronized float getTotalFPS() {
        return fpsTotal;
    }
}