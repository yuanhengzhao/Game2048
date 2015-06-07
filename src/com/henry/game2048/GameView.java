package com.henry.game2048;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameView extends LinearLayout {

	public GameView(Context context) {
		super(context);

		initGameView();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initGameView();
	}

	private void initGameView(){
		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(0xffbbada0);


		setOnTouchListener(new View.OnTouchListener() {

			private float startX, startY, offsetX, offsetY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						startX = event.getX();
						startY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						offsetX = event.getX() - startX;
						offsetY = event.getY() - startY;

						if (offsetX > 0 && offsetY > 0 && offsetX < 2 * offsetY && 0.5 * offsetY < offsetX) {
							swipeSoutheast();
							//Toast.makeText(MainActivity.getMainActivity(), "Southeast", Toast.LENGTH_SHORT).show();
						} else if (offsetX > 0 && offsetX > 2 * Math.abs(offsetY)) {
							swipeEast();
						} else if (offsetX > 0 && offsetY < 0 && offsetX < -2 * offsetY && -0.5 * offsetY < offsetX) {
							swipeNortheast();
							//Toast.makeText(MainActivity.getMainActivity(), "Northeast", Toast.LENGTH_SHORT).show();
						} else if (offsetY < 0 && -offsetY > 2 * Math.abs(offsetX)) {
							swipeNorth();
						} else if (offsetX < 0 && offsetY < 0 && offsetX > 2 * offsetY && 0.5 * offsetY > offsetX) {
							//Toast.makeText(MainActivity.getMainActivity(), "Northwest", Toast.LENGTH_SHORT).show();
							swipeNorthwest();
						} else if (offsetX < 0 && -offsetX > 2 * Math.abs(offsetY)) {
							swipeWest();
						} else if (offsetX < 0 && offsetY > 0 && offsetX > -2 * offsetY && 0.5 * offsetY > offsetX) {
							swipeSouthwest();
							 //Toast.makeText(MainActivity.getMainActivity(), "Southwest", Toast.LENGTH_SHORT).show();
						} else if (offsetY > 0 && offsetY > 2 && offsetY > Math.abs(offsetX)) {
							swipeSouth();
						}

//					if (Math.abs(offsetX) > Math.abs(offsetY)) {
//						if (offsetX < -100) {
//							swipeLeft();
//						} else if (offsetX > 100) {
//							swipeRight();
//						}
//					} else {
//						if (offsetY < -100) {
//							swipeUp();
//						} else if (offsetY > 100) {
//							swipeDown();
//						}
//					}

						break;
				}
				return true;
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		Config.CARD_WIDTH = (Math.min(w, h)-10)/Config.LINES;

		addCards(Config.CARD_WIDTH, Config.CARD_WIDTH);

		startGame();
	}

	private void addCards(int cardWidth,int cardHeight){

		Card c;

		LinearLayout line;
		LinearLayout.LayoutParams lineLp;
		
		for (int y = 0; y < Config.LINES; y++) {
			line = new LinearLayout(getContext());
			lineLp = new LinearLayout.LayoutParams(-1, cardHeight);
			addView(line, lineLp);
			
			for (int x = 0; x < Config.LINES; x++) {
				c = new Card(getContext());
				line.addView(c, cardWidth, cardHeight);

				cardsMap[x][y] = c;
			}
		}
	}

	public void startGame(){

		MainActivity aty = MainActivity.getMainActivity();
		aty.clearScore();
		aty.showBestScore(aty.getBestScore());

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				cardsMap[x][y].setNum(0);
			}
		}

		addRandomNum();
		//addRandomNum();
	}

	private void addRandomNum(){

		emptyPoints.clear();

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardsMap[x][y].getNum()<=0) {
					emptyPoints.add(new Point(x, y));
				}
			}
		}

		if (emptyPoints.size()>0) {

			Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
			cardsMap[p.x][p.y].setNum(Math.random() > 0.1 ? 2 : 4);

			MainActivity.getMainActivity().getAnimLayer().createScaleTo1(cardsMap[p.x][p.y]);
		}
	}


	private void swipeWest(){

		boolean merge = false;

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {

				for (int x1 = x+1; x1 < Config.LINES; x1++) {
					if (cardsMap[x1][y].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {

							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y],cardsMap[x][y], x1, x, y, y);

							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);

							x--;
							merge = true;

						}else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y],x1, x, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);

							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}


						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeNorthwest(){
		int b, x, y;
		boolean merge = false;
		for (b=-2; b<3; b++){
		for ( y = (b + Math.abs(b))/2; y<Config.LINES-(Math.abs(b)-b)/2; y++) {

				for (int y1 = y+1; y1 < Config.LINES-(Math.abs(b)-b)/2; y1++) {
					    x = y-b;
					int x1= y1-b;
					if (cardsMap[x1][y1].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {

							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1],cardsMap[x][y], x1, x, y1, y);

							cardsMap[x][y].setNum(cardsMap[x1][y1].getNum());
							cardsMap[x1][y1].setNum(0);

							y--;
							x--;
							merge = true;

						}else if (cardsMap[x][y].equals(cardsMap[x1][y1])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1], cardsMap[x][y],x1, x1, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y1].setNum(0);

							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeEast(){

		boolean merge = false;

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = Config.LINES-1; x >=0; x--) {

				for (int x1 = x-1; x1 >=0; x1--) {
					if (cardsMap[x1][y].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y], x1, x, y, y);
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);

							x++;
							merge = true;
						}else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y],x1, x, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y].setNum(0);
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeSoutheast(){
		int b, x, y;
		boolean merge = false;
		for (b=-2; b<3; b++){
			for ( y =Config.LINES-(Math.abs(b)-b)/2 -1; y>=(b + Math.abs(b))/2; y--) {

				for (int y1 = y-1; y1 >= (b + Math.abs(b))/2; y1--) {
					x = y-b;
					int x1= y1-b;
					if (cardsMap[x1][y1].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {

							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1],cardsMap[x][y], x1, x, y1, y);

							cardsMap[x][y].setNum(cardsMap[x1][y1].getNum());
							cardsMap[x1][y1].setNum(0);

							y++;
							x++;
							merge = true;

						}else if (cardsMap[x][y].equals(cardsMap[x1][y1])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1], cardsMap[x][y],x1, x1, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y1].setNum(0);

							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeNorth(){

		boolean merge = false;

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = 0; y < Config.LINES; y++) {

				for (int y1 = y+1; y1 < Config.LINES; y1++) {
					if (cardsMap[x][y1].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x][y1], cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);

							y--;

							merge = true;
						}else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x][y1],cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}

						break;

					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeNortheast(){
		int b, x, y;
		boolean merge = false;
		for (b=1; b<6; b++){
			for ( y = (b-3 + Math.abs(b-3))/2; y<Config.LINES-(Math.abs(b-3)-b+3)/2; y++) {

				for (int y1 = y+1; y1 < Config.LINES-(Math.abs(b-3)-b+3)/2; y1++) {
					x = -y+b;
					int x1= -y1+b;
					if (cardsMap[x1][y1].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {

							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1],cardsMap[x][y], x1, x, y1, y);

							cardsMap[x][y].setNum(cardsMap[x1][y1].getNum());
							cardsMap[x1][y1].setNum(0);

							y--;
							x++;
							merge = true;

						}else if (cardsMap[x][y].equals(cardsMap[x1][y1])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1], cardsMap[x][y],x1, x1, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y1].setNum(0);

							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeSouth(){

		boolean merge = false;

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = Config.LINES-1; y >=0; y--) {

				for (int y1 = y-1; y1 >=0; y1--) {
					if (cardsMap[x][y1].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x][y1], cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);

							y++;
							merge = true;
						}else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x][y1],cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x][y1].setNum(0);
							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void swipeSouthwest(){
		int b, x, y;
		boolean merge = false;
		for (b=1; b<6; b++){
			for ( y =Config.LINES-(Math.abs(b-3)-b+3)/2-1; y>=(b-3 + Math.abs(b-3))/2; y--) {

				for (int y1 = y-1; y1 >= (b-3 + Math.abs(b-3))/2; y1--) {
					x = -y+b;
					int x1= -y1+b;
					if (cardsMap[x1][y1].getNum()>0) {

						if (cardsMap[x][y].getNum()<=0) {

							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1],cardsMap[x][y], x1, x, y1, y);

							cardsMap[x][y].setNum(cardsMap[x1][y1].getNum());
							cardsMap[x1][y1].setNum(0);

							y++;
							x--;
							merge = true;

						}else if (cardsMap[x][y].equals(cardsMap[x1][y1])) {
							MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y1], cardsMap[x][y],x1, x1, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
							cardsMap[x1][y1].setNum(0);

							MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}
	private void checkComplete(){

		boolean complete = true;

		ALL:
			for (int y = 0; y < Config.LINES; y++) {
				for (int x = 0; x < Config.LINES; x++) {
					if (cardsMap[x][y].getNum()==0||
							(x>0&&cardsMap[x][y].equals(cardsMap[x-1][y]))||
							(x<Config.LINES-1&&cardsMap[x][y].equals(cardsMap[x+1][y]))||
							(y>0&&cardsMap[x][y].equals(cardsMap[x][y-1]))||
							(y<Config.LINES-1&&cardsMap[x][y].equals(cardsMap[x][y+1]))) {

						complete = false;
						break ALL;
					}
				}
			}

		if (complete) {
			new AlertDialog.Builder(getContext()).setTitle("Hello").setMessage("Game Over").setPositiveButton("Restart", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					startGame();
				}
			}).show();
		}

	}

	private Card[][] cardsMap = new Card[Config.LINES][Config.LINES];
	private List<Point> emptyPoints = new ArrayList<Point>();
}
