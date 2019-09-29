package app.temp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class LayoutFaceService extends CanvasWatchFaceService {

    private boolean isZoneReceiverRegistered = false;
    private View layoutRoot;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        static final int MSG_UPDATE_TIME = 0;
        final Handler updateTimeHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        updateTimer();
                        break;
                }
                
                return false;
            }
        });

        final BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layoutRoot = inflater.inflate(R.layout.circle_layout, null);
            RelativeLayout interiorLayout = (RelativeLayout) layoutRoot.findViewById(R.id.interior);

            DifferentColorCircularBorder border = new DifferentColorCircularBorder(interiorLayout);


            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleRed), 0, 40);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleGreen), 40, 80);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleYellow), 80, 120);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleBlue), 120, 160);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleYellow), 160, 200);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleRed), 200, 240);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleGreen), 240, 280);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleBlue), 280, 320);
            border.addBorderPortion(getApplicationContext(), ContextCompat.getColor(getApplicationContext(), R.color.colorGoogleYellow), 320, 360);

        }

        @Override
        public void onDestroy() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerZoneReceiver();
            } else {
                unregisterZoneReceiver();
            }
            updateTimer();
        }

        private void registerZoneReceiver() {
            if (isZoneReceiverRegistered) {
                return;
            }
            isZoneReceiverRegistered = true;
            LayoutFaceService.this.registerReceiver(timeZoneReceiver, 
                    new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED));
        }

        private void unregisterZoneReceiver() {
            if (!isZoneReceiverRegistered) {
                return;
            }
            LayoutFaceService.this.unregisterReceiver(timeZoneReceiver);
            isZoneReceiverRegistered = false;
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            int gravity = !insets.isRound() ? (Gravity.START | Gravity.TOP) :  Gravity.CENTER;
            setWatchFaceStyle(new WatchFaceStyle.Builder(LayoutFaceService.this)
                    .setStatusBarGravity(gravity)
                    .setAccentColor(0xff526cfe)
                    .build());
            Point displaySize = new Point();
            ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getSize(displaySize);

            int specW = View.MeasureSpec.makeMeasureSpec(displaySize.x, View.MeasureSpec.EXACTLY);
            int specH = View.MeasureSpec.makeMeasureSpec(displaySize.y, View.MeasureSpec.EXACTLY);
            layoutRoot.measure(specW, specH);
            layoutRoot.layout(0, 0, layoutRoot.getMeasuredWidth(), layoutRoot.getMeasuredHeight());
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (inAmbientMode) {
                postInvalidate();
            }
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            canvas.drawColor(Color.BLACK);
            layoutRoot.draw(canvas);
        }
        private void updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            
            if (isVisible() && !isInAmbientMode()) {
                final long delayMs = DateUtils.SECOND_IN_MILLIS -
                        (System.currentTimeMillis() % DateUtils.SECOND_IN_MILLIS);
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

    }
}
