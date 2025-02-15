package com.example.zadanie6pum;

import static java.lang.String.valueOf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.FloatProperty;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
   private enum RockfordDo {stand,idle,left,right,appear,die};

   private int frameCount = 7; // Ilość klatek w spritesheecie
   private int frameWidth; // szerokość pojedynczej klatki

   private GridLayout boardGridLayout; // GridLayout dla planszy
   private Character mapa[][]={
           {'b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b'},
           {'b','e','s','g','g','s','d','g','w','g','s','g','g','g','s','g','w','s','g','g','e','g','s','w','g','g','e','g','g','b'},
           {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','s','g','e','g','g','w','s','g','g','g','g','b'},
           {'b','e','e','e','e','e','e','s','e','e','s','e','e','e','e','s','e','g','g','e','x','e','e','e','g','e','e','s','g','b'},
           {'b','d','g','g','g','g','g','g','w','g','s','g','g','g','g','g','w','e','s','g','e','g','g','w','g','g','g','s','d','b'},
           {'b','g','g','g','g','g','g','m','w','g','s','g','g','g','g','s','w','s','s','g','e','g','g','w','g','g','g','s','d','b'},
           {'b','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},
           {'b','g','s','g','g','s','g','g','w','g','s','g','g','g','s','s','w','g','g','g','e','g','s','w','g','g','e','g','g','b'},
           {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','g','g','w','g','g','g','e','g','g','w','x','s','g','g','g','b'},
           {'b','e','e','e','x','e','e','e','e','e','e','e','e','x','e','g','e','s','s','e','e','e','e','e','e','g','e','s','g','b'},
           {'b','m','g','g','g','g','g','s','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','d','b'},
           {'b','g','g','g','d','g','s','s','w','g','s','g','g','g','g','s','w','g','s','g','e','g','g','w','g','g','g','s','g','b'},
           {'b','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},
           {'b','g','s','g','g','s','d','g','w','g','s','g','g','s','g','g','w','g','s','g','e','m','s','w','g','x','e','g','g','b'},
           {'b','g','g','g','g','g','g','g','w','g','g','g','g','g','s','s','w','g','g','g','e','g','g','w','g','x','g','g','g','b'},
           {'b','e','e','e','e','e','e','e','e','e','e','e','x','g','g','g','e','s','s','e','e','e','e','e','e','s','e','s','g','b'},
           {'b','g','g','g','g','g','g','g','w','g','s','g','g','g','e','e','w','g','g','g','e','g','g','w','g','s','g','s','d','b'},
           {'b','d','g','g','g','g','g','m','w','g','s','g','g','g','g','s','w','g','g','g','e','g','g','w','g','g','g','s','d','b'},
           {'b','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','w','g','w','w','e','w','w','w','w','w','w','w','w','b'},
           {'b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b','b'},
   }; //mapa

   private ImageView imageView;
   private int frameIndex = 0;
   private Handler handler;
   private Bitmap spritesheet_rockford_stand;
   private Bitmap spritesheet_rockford_idle;
   private Bitmap spritesheet_rockford_left;
   private Bitmap spritesheet_rockford_right;
   private Bitmap spritesheet_mob;
   private Bitmap spritesheet_butterfly;
   private Bitmap spritesheet_diamond;
   private RockfordDo rockfordDo = RockfordDo.stand;
   private int idleTimer=0;
   private int idletime=40;
   private int scale = 1; // Współczynnik powiększenia
   private float imageX = 88 , imageY = 88;  // współrzędne Rockforda
   private int mapX = 1 , mapY = 1;  // współrzędne Rockforda na mapie
   private boolean movingUp, movingDown, movingLeft, movingRight;
   private float deltaX = 88; // przesunięcie Rockforda prawo lewo
   private float deltaY = 88; // przesunięcie Rockforda góra dół
   private int map_change = 0; // ilość przesunięć mapy
   private int map_direction_move = 0; // kierunek przesuniecia mapy
   int tile_resource;
   final int victory_diamonds = 10;
   boolean rock_is_moving = false;

   int rockfordPoints = 0; // liczba punktów (zebanych diamentów)
   int rockfordLifes; // liczba żyć

   TextView tv_info;

   SharedPreferences game_data; // współzielenie danych pomiędzy aktywnościami
   SharedPreferences.Editor game_data_editor;

   private int time_count = 0; // mierzenie czasu gry
   private Timer timer_game;

   private MediaPlayer sound_play;
   boolean first_step_sound = true;


   // Definicja obszarów sterowania
   private View topControl, bottomControl, leftControl, rightControl;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      FrameLayout mainLayout = findViewById(R.id.mainLayout); // Pobieramy RelativeLayout z layoutu
      mainLayout.setBackgroundColor(getResources().getColor(android.R.color.black)); // Ustawiamy czarne tło
      // Inicjalizacja planszy w GridLayout
      initBoardGridLayout();

      sound_play = new MediaPlayer();
      tv_info = findViewById(R.id.tv_info);
      imageView = findViewById(R.id.sprite_view);
      handler = new Handler();
      spritesheet_rockford_stand = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_stand_32x32);
      spritesheet_rockford_idle = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_idle_32x32);
      spritesheet_rockford_left = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_left_32x32);
      spritesheet_rockford_right = BitmapFactory.decodeResource(getResources(),R.drawable.rockford_right_32x32);
      spritesheet_mob = BitmapFactory.decodeResource(getResources(),R.drawable.mob_32x32_sprite);
      spritesheet_butterfly = BitmapFactory.decodeResource(getResources(),R.drawable.butterfly_32x32_sprite);
      spritesheet_diamond = BitmapFactory.decodeResource(getResources(),R.drawable.diamond_32x32_sprite);
      // Obliczamy szerokość pojedynczej klatki
      frameWidth = spritesheet_rockford_stand.getWidth() / frameCount;
      // Ustawiamy szerokość ImageView na szerokość pojedynczej klatki, pomnożoną przez współczynnik powiększenia
      imageView.getLayoutParams().width = frameWidth * scale;
      imageView.getLayoutParams().height = spritesheet_rockford_stand.getHeight() * scale; // Ustawiamy wysokość proporcjonalnie do powiększenia
      // imageView.requestLayout();
      // Inicjalizacja obszarów sterowania
      topControl = findViewById(R.id.controlTop);
      bottomControl = findViewById(R.id.controlDown);
      leftControl = findViewById(R.id.controlLeft);
      rightControl = findViewById(R.id.controlRight);
      // Ustawiamy obszary sterowania jako dotykowe
      setTouchListeners();
      // Uruchamiamy animację
      startAnimation();

      blocksAnimation();
      //startAnimationTexture();

      game_data = getSharedPreferences("game_data", MODE_PRIVATE);
      game_data_editor = game_data.edit();
      rockfordLifes = game_data.getInt("game_lifes", 5);

      showInformation(); // Wyświetlanie liczby żyć i punktów

      loadTimer();

      playSound(R.raw.start_game);
   }

   private void initBoardGridLayout() {
      // Tworzymy planszę w GridLayout
      boardGridLayout = new GridLayout(this);
      boardGridLayout.setColumnCount(mapa[0].length); // liczba kolumn
      boardGridLayout.setRowCount(mapa.length);    // liczba wierszy
      // Tworzymy obiekty ImageView i dodajemy je do planszy
      for (int i = 0; i < mapa.length; i++) {
         for (int j = 0; j < mapa[0].length; j++) {
            ImageView imageView = new ImageView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            //params.height = 64;
            //params.width = 64;
            //imageView.setLayoutParams(params);
            switch (mapa[i][j]){
               case 'b':
                  imageView.setImageResource(R.drawable.border_32x32);                  break;
               case 'w':
                  imageView.setImageResource(R.drawable.wall_32x32);                  break;
               case 's':
                  imageView.setImageResource(R.drawable.stone_32x32);                  break;
               case 'g':
                  imageView.setImageResource(R.drawable.ground_32x32);                  break;
               case 'e':
                  imageView.setImageResource(R.drawable.empty_32x32);                  break;
               case 'm':
                  imageView.setImageResource(R.drawable.mob_32x32);                  break;
               case 'd':
                  imageView.setImageResource(R.drawable.diamond_32x32);                  break;
               case 'x':
                  imageView.setImageResource(R.drawable.butterfly_32x32);                  break;
            }
            boardGridLayout.addView(imageView);
         }
      }
      // Pobieramy FrameeLayout z layoutu
      FrameLayout mainLayout = findViewById(R.id.mainLayout);
      // Dodajemy planszę do RelativeLayout
      mainLayout.addView(boardGridLayout,0);
   }

   private void setTouchListeners() {
      topControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingUp = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingUp = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });

      bottomControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingDown = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingDown = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });

      leftControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingLeft = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingLeft = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });

      rightControl.setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  movingRight = true;
                  idleTimer=0;
                  break;
               case MotionEvent.ACTION_UP:
                  movingRight = false;
                  rockfordDo=RockfordDo.stand;
                  break;
            }
            return true;
         }
      });
   }

   private void adjustRockfordSpritePositionToScroll() {
      // Pozykanie pozycji Rockforda w pixelach pod przesunięcie ekranu
      Map<Character, Integer> rockfordPixel = getCurrentRockfordPosition();
      imageX = rockfordPixel.get('x');
      imageY = rockfordPixel.get('y');

      imageView.setX(imageX);
      imageView.setY(imageY);
   }

   private Map<Character, Integer> getCurrentRockfordPosition() {
      float scrollX = boardGridLayout.getScrollX();
      float scrollY = boardGridLayout.getScrollY();

      //Calculate the position of Rockford in pixels
      int rockfordPixelX = (int) (mapX * deltaX);
      int rockfordPixelY = (int) (mapY * deltaY);
      //int rockfordPixelX = Math.round(imageY / deltaY);
      //int rockfordPixelY = Math.round(imageX / deltaX);


      Map<Character, Integer> position_map = new HashMap<>();
      position_map.put('x', (int) (rockfordPixelX - scrollX));
      position_map.put('y', (int) (rockfordPixelY - scrollY));
      return position_map;
      //return Map.of('x', imageX - scrollX, 'y', imageY - scrollY);
   }

   private void scrollMap() {
      // Get the viewport size
      DisplayMetrics displayMetrics = new DisplayMetrics();
      getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      int height = displayMetrics.heightPixels;
      int width = displayMetrics.widthPixels;
      if (imageX < 5 * deltaX && boardGridLayout.getScrollX() > 0) {
         boardGridLayout.scrollBy((int) -deltaX, 0);
         map_direction_move = -1;
         map_change--;

      } else if (width - imageX < 5 * deltaX && boardGridLayout.getScrollX() < mapa[0].length * deltaX - width) {
         boardGridLayout.scrollBy((int) deltaX, 0);
         map_change++;
         map_direction_move = 1;
      }
   }



   // Funkcja decydująca w którym kierunku Rockford ma się poruszyć w zależności od aktualnego stanu ruchu
   private void moveRockford() {
      if (movingUp && canMoveTo(mapY - 1, mapX)) {
         playSoundOfMove();
         mapY--;
         rockfordDo = RockfordDo.left;
      }
      if (movingDown && canMoveTo(mapY + 1, mapX)) {
         playSoundOfMove();
         mapY++;
         rockfordDo = RockfordDo.left;
      }
      if (movingLeft && canMoveTo(mapY, mapX - 1)) {
         playSoundOfMove();
         mapX--;
         rockfordDo = RockfordDo.left;
      }
      if (movingRight && canMoveTo(mapY, mapX + 1)) {
         playSoundOfMove();
         mapX++;
         rockfordDo = RockfordDo.right;
      }

      handleTileUpdate(mapY, mapX);
      adjustRockfordSpritePositionToScroll();
      scrollMap();
   }

   // Funkcja sprawdza, czy Rockford może poruszyć się na pole o współrzędnych
   private boolean canMoveTo(int row, int col) {
      return row >= 0 && row < mapa.length && col >= 0 && col < mapa[0].length &&
              (mapa[row][col] == 'g' || mapa[row][col] == 'e' || mapa[row][col] == 'd' || mapa[row][col] == 'm');
   }


   // Funkcja aktualizuje stan mapy po ruchu Rockforda
   private void handleTileUpdate(int row, int col) {
      if (mapa[row][col] == 'd') {
         rockfordPoints++;
         playSound(R.raw.diamond_collect);
         showInformation();
         isGameLooseOrVictory();
      }

      if (mapa[row][col] == 'g' || mapa[row][col] == 'd') {
         mapa[row][col] = 'e';
         updateSingleTile((ImageView) boardGridLayout.getChildAt(row * boardGridLayout.getColumnCount() + col), 'e');
      }

      if (mapa[row][col] == 'm' || mapa[row][col] == 'x' || mapa[row][col] == 's') {
         rockfordDead();
      }

      if (mapa[row][col] == 'd' || mapa[row-1][col] == 's') {
         fallingStone(row, col);
      }
   }

   private void fallingStone(int row, int col) {
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
            rock_is_moving = true;
            if (mapa[row-1][col] == 's' && mapa[row][col] == 'e') {
               mapa[row-1][col] = 'e';
               mapa[row][col] = 's';
               blocksAnimation();
               fallingStone((row+1), col);
               playSound(R.raw.stone_fall);
            }
         }
      }, 500);
   }

   private void blocksAnimation() {
      // Usuwamy wszystkie widoki z GridLayout
      boardGridLayout.removeAllViews();

      // Dodajemy widoki zgodnie z aktualnym stanem mapy
      for (int i = 0; i < mapa.length; i++) {
         for (int j = 0; j < mapa[0].length; j++) {
            ImageView imageView = new ImageView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            switch (mapa[i][j]) {
               case 'b':
                  imageView.setImageResource(R.drawable.border_32x32);
                  break;
               case 'w':
                  imageView.setImageResource(R.drawable.wall_32x32);
                  break;
               case 's':
                  imageView.setImageResource(R.drawable.stone_32x32);
                  break;
               case 'g':
                  imageView.setImageResource(R.drawable.ground_32x32);
                  break;
               case 'e':
                  imageView.setImageResource(R.drawable.empty_32x32);
                  break;
               case 'm':
                  AnimationDrawable mobAnimation = new AnimationDrawable();
                  for (int k = 0; k < frameCount; k++) {
                     Bitmap frame = Bitmap.createBitmap(spritesheet_mob, k * frameWidth, 0, frameWidth, spritesheet_mob.getHeight());
                     frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_mob.getHeight() * scale, true);
                     mobAnimation.addFrame(new BitmapDrawable(getResources(), frame), 300);
                  }
                  mobAnimation.setOneShot(false);
                  imageView.setImageDrawable(mobAnimation);
                  mobAnimation.start();
                  break;
               case 'd':
                  // Create an animation drawable for the diamond
                  AnimationDrawable diamondAnimation = new AnimationDrawable();
                  for (int k = 0; k < frameCount; k++) {
                     Bitmap frame = Bitmap.createBitmap(spritesheet_diamond, k * frameWidth, 0, frameWidth, spritesheet_diamond.getHeight());
                     frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_diamond.getHeight() * scale, true);
                     diamondAnimation.addFrame(new BitmapDrawable(getResources(), frame), 300);
                  }
                  diamondAnimation.setOneShot(false);
                  imageView.setImageDrawable(diamondAnimation);
                  diamondAnimation.start();
                  break;
               case 'x':
                  // Create an animation drawable for the butterfly
                  AnimationDrawable butterflyAnimation = new AnimationDrawable();
                  for (int k = 0; k < frameCount; k++) {
                     Bitmap frame = Bitmap.createBitmap(spritesheet_butterfly, k * frameWidth, 0, frameWidth, spritesheet_butterfly.getHeight());
                     frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_butterfly.getHeight() * scale, true);
                     butterflyAnimation.addFrame(new BitmapDrawable(getResources(), frame), 300);
                  }
                  butterflyAnimation.setOneShot(false);
                  imageView.setImageDrawable(butterflyAnimation);
                  butterflyAnimation.start();
                  break;
            }
            boardGridLayout.addView(imageView);
         }
      }
   }

   // Funkcja aktualizująca grafikę pola w ImageView
   private void updateSingleTile(ImageView imageView, char tile_type) {
      switch (tile_type) {
         case 'b':
            imageView.setImageResource(R.drawable.border_32x32); break;
         case 'w':
            imageView.setImageResource(R.drawable.wall_32x32); break;
         case 's':
            imageView.setImageResource(R.drawable.stone_32x32); break;
         case 'g':
            imageView.setImageResource(R.drawable.ground_32x32); break;
         case 'e':
            imageView.setImageResource(R.drawable.empty_32x32); break;
         case 'm':
            imageView.setImageResource(R.drawable.mob_32x32); break;
         case 'd':
            imageView.setImageResource(R.drawable.diamond_32x32); break;
         case 'x':
            imageView.setImageResource(R.drawable.butterfly_32x32); break;
      }
      imageView.setImageResource(tile_resource);
   }

   private void rockfordDead() { // Funckja śmierci Rockforda
      rockfordLifes--;
      imageX = 88;
      imageY = 88;
      mapX = 1;
      mapY = 1;

      scrollMap();
      showInformation();
      isGameLooseOrVictory();
      playSound(R.raw.rockford_appear);
   }

   private void isGameLooseOrVictory() {
      if (rockfordLifes == 0) { // Warunek przegranej
         game_data_editor.putInt("time_of_game", time_count);
         game_data_editor.putInt("destroyed_circles", rockfordPoints);

         Intent intent = new Intent(this, GameOverActivity.class);
         startActivity(intent);
      }
      if (rockfordPoints >= victory_diamonds) {
         timer_game.cancel();

         game_data_editor.putInt("time_of_game", time_count);
         game_data_editor.putInt("destroyed_circles", rockfordPoints);
         game_data_editor.putBoolean("victoria_check", true);

         Intent intent = new Intent(this, GameOverActivity.class);
         startActivity(intent);
      }

      // Pobranie danych o najlepszych wynikach
      int all_game_time = game_data.getInt("all_time_of_game", 0);
      int all_game_points = game_data.getInt("all_destroyed_circles", 0);
      // Dodawanie kolejnych rekordów do sumarycznych wyników
      game_data_editor.putInt("all_time_of_game", all_game_time + time_count);
      game_data_editor.putInt("all_destroyed_circles", all_game_points + rockfordPoints);
      game_data_editor.commit();

   }

   // Wyświetlanie informacji po utracie życia
   private void showInformation() {
      tv_info.setText("Życia: " + rockfordLifes + "\n" + "Punkty: " + rockfordPoints);
      tv_info.setVisibility(View.VISIBLE);

      // Ukrycie TextView po 2 sekundach
      new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
            tv_info.setVisibility(View.GONE);
         }
      }, 2000);
   }

   private void startAnimation() {
      Runnable animationRunnable = new Runnable() {
         @Override
         public void run() {
            // Ustawiamy kolejną klatkę animacji
            // Wycinamy fragment z spritesheeta odpowiadający danej klatce i ustawiamy w ImageView
            Bitmap frame;
            switch (rockfordDo){
               case stand:
                  frame = Bitmap.createBitmap(spritesheet_rockford_stand, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_stand.getHeight());
                  frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_stand.getHeight() * scale, true);
                  moveRockford();
                  idleTimer++;
                  imageView.setImageBitmap(frame);
                  if (idleTimer>20) {rockfordDo=RockfordDo.idle;}
                  break;
               case idle:
                  frame = Bitmap.createBitmap(spritesheet_rockford_idle, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_idle.getHeight());
                  frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_idle.getHeight() * scale, true);
                  moveRockford();
                  imageView.setImageBitmap(frame);
                  break;
               case right:
                  frame = Bitmap.createBitmap(spritesheet_rockford_right, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_right.getHeight());
                  frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_right.getHeight() * scale, true);
                  moveRockford();
                  imageView.setImageBitmap(frame);
                  idleTimer=0;
                  break;
               case left:
                  frame = Bitmap.createBitmap(spritesheet_rockford_left, frameIndex * frameWidth, 0, frameWidth, spritesheet_rockford_left.getHeight());
                  frame = Bitmap.createScaledBitmap(frame, frameWidth * scale, spritesheet_rockford_left.getHeight() * scale, true);
                  moveRockford();
                  imageView.setImageBitmap(frame);
                  idleTimer=0;
                  break;
            }

            // Sprawdzenie kolizji ze skałą
            int rockfordRow = mapY;
            int rockfordCol = mapX;
            if (mapa[rockfordRow][rockfordCol] == 's') {
               rockfordDead();
            }

            // Inkrementujemy indeks klatki
            frameIndex = (frameIndex + 1) % frameCount;
            // Wywołujemy ponownie runnable po pewnym czasie dla następnej klatki
            handler.postDelayed(this, 100); // Ustaw czas opóźnienia między klatkami (tutaj 100ms)
         }
      };
      handler.post(animationRunnable);
   }

   // Uruchomienie mierzenia czasu
   private void loadTimer() {
      timer_game = new Timer();
      timer_game.schedule(new TimerTask() {
         @Override
         public void run() {
            time_count++;
            //is_timer_active = true;
            //System.out.println(time_count);
         }
      }, 1000, 1000);
   }

   //Funkcja odtwarzająca dźwięki
   private void playSound(int tempSound) {
      try {
         sound_play.reset();
         sound_play = MediaPlayer.create(this, tempSound);
         sound_play.start();
      } catch (Exception temp) {
         temp.printStackTrace();
      }
   }

   private void playSoundOfMove() {
      if (first_step_sound) {
         playSound(R.raw.krok);
         first_step_sound = false;
      } else {
         playSound(R.raw.krok2);
         first_step_sound = true;
      }
   }

   @Override
   protected void onDestroy() {
      if (sound_play!=null){
         sound_play.release();
         sound_play=null;
      }
      super.onDestroy();
      handler.removeCallbacksAndMessages(null);
      spritesheet_rockford_stand.recycle(); // Zwolnij zasoby bitmapy spritesheeta
      spritesheet_rockford_left.recycle(); // Zwolnij zasoby bitmapy spritesheeta
      spritesheet_rockford_right.recycle(); // Zwolnij zasoby bitmapy spritesheeta
   }
}