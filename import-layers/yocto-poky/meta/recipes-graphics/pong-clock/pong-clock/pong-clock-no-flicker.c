/*
 * Pong Clock - A clock that plays pong. 
 *             See http://mocoloco.com/archives/001766.php for the inspiration.
 *
 * Copyright (C) 2005 Matthew Allum
 *
 * Author: Matthew Allum mallum@openedhand.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */

#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <sys/types.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <signal.h>

#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xatom.h>

/* Tweak values for different hw setups */

#define FPS          50
#define RESX         40
#define RESY         40
#define TO_MISS_SECS 55
#define BALLDX       16
#define BALLDY       4


typedef struct PongClock
{
  Display *xdpy;
  int      xscreen;
  Window   xwin, xwin_root;
  Pixmap   backbuffer;
  GC       xgc;
  int      xwin_width, xwin_height;
  int      pixelw, pixelh;

  int      ball_x, ball_y, ball_dx, ball_dy;
  int      bata_y, batb_y;
  Bool     bata_to_miss, batb_to_miss;

} 
PongClock;

void
get_time(int *hour, int *min, int *sec)
{
  struct timeval   tv;
  struct tm       *localTime = NULL; 
  time_t           actualTime;

  gettimeofday(&tv, 0);
  actualTime = tv.tv_sec;
  localTime = localtime(&actualTime);

  if (hour)
    *hour = localTime->tm_hour;
  
  if (min)
    *min  = localTime->tm_min;

  if (sec)
    *sec  = localTime->tm_sec;
}

void
draw_rect (PongClock *pong_clock, 
	   int        x,
	   int        y,
	   int        width,
	   int        height)
{
  XFillRectangle (pong_clock->xdpy,
		  pong_clock->backbuffer,
		  pong_clock->xgc,
		  x * pong_clock->pixelw,
		  y * pong_clock->pixelh,
		  width * pong_clock->pixelw,
		  height * pong_clock->pixelh);
}

void
draw_field (PongClock *pong_clock)
{
  int i;

  draw_rect (pong_clock, 0, 0, RESX+1, 1);  
  draw_rect (pong_clock, 0, RESY-1, RESX+1, 1);  

  for (i=0; i < RESY/2; i++)
    draw_rect (pong_clock, (RESX/2)-1, i*2, 2, 1);  
}

void
draw_digit (PongClock *pong_clock, 
	    int        x,
	    int        y,
	    int        digit)
{
  int digits[] = { 0x1f8c63f, 0x1f21086, 0x1f0fe1f, 0x1f87e1f, 0x1087e31,
		   0x1f87c3f, 0x1f8fc3f, 0x84421f,  0x1f8fe3f, 0x1087e3f };

  XRectangle rects[5*5];
  int i,j,k;

  i = 0;

  for (k=0; k<5; k++) 
    for (j=0; j<5; j++)	
      if (digits[digit] & (1 << ((k*5)+j)))
	{
	  rects[i].x      = (x + j) * pong_clock->pixelw; 
	  rects[i].y      = (y + k) * pong_clock->pixelh; 
	  rects[i].width  = pong_clock->pixelw;
	  rects[i].height = pong_clock->pixelh;  
	  i++;
	}

  XFillRectangles (pong_clock->xdpy,
		   pong_clock->backbuffer,
		   pong_clock->xgc,
		   rects, i);
} 

void
draw_time (PongClock *pong_clock)
{
  int hour, min;

  get_time(&hour, &min, NULL);

  draw_digit (pong_clock, 
	      (RESX/2) - 14,
	      5,
	      hour / 10 );

  draw_digit (pong_clock, 
	      (RESX/2) - 8,
	      5,
	      hour % 10 );

  draw_digit (pong_clock, 
	      (RESX/2) + 3,
	      5,
	      min / 10 );

  draw_digit (pong_clock, 
	      (RESX/2) + 9,
	      5,
	      min % 10 );
}

void
draw_bat_and_ball (PongClock *pong_clock)
{
  /* ball */

  XFillRectangle (pong_clock->xdpy,
		  pong_clock->backbuffer,
		  pong_clock->xgc,
		  pong_clock->ball_x,
		  pong_clock->ball_y,
		  pong_clock->pixelw,
		  pong_clock->pixelh);

  /* bat a */

  XFillRectangle (pong_clock->xdpy,
		  pong_clock->backbuffer,
		  pong_clock->xgc,
		  0,
		  pong_clock->bata_y - (2 * pong_clock->pixelh),
		  pong_clock->pixelw,
		  pong_clock->pixelh * 5);

  /* bat b */

  XFillRectangle (pong_clock->xdpy,
		  pong_clock->backbuffer,
		  pong_clock->xgc,
		  (pong_clock->xwin_width - pong_clock->pixelw),
		  pong_clock->batb_y - (2 * pong_clock->pixelh),
		  pong_clock->pixelw,
		  pong_clock->pixelh * 5);

}

void
update_state (PongClock *pong_clock)
{
  int sec, min, hour;

  get_time(&hour, &min, &sec);

  /* Check ball is on field and no ones dues to miss a shot.
  */
  if ( (pong_clock->ball_x < 0 && !pong_clock->bata_to_miss) 
      || (pong_clock->ball_x > (pong_clock->xwin_width - pong_clock->pixelw)
	  && !pong_clock->batb_to_miss) )
    pong_clock->ball_dx *= -1;

  if ((pong_clock->ball_y < pong_clock->pixelh)
      || pong_clock->ball_y > (pong_clock->xwin_height - (2*pong_clock->pixelh)))
    pong_clock->ball_dy *= -1; 

  pong_clock->ball_x += pong_clock->ball_dx;
  pong_clock->ball_y += pong_clock->ball_dy;

  /* Set up someone to miss if we getting close to an hour or min. 
   */
  if (sec > TO_MISS_SECS)
    {
      if (min == 59)
	pong_clock->batb_to_miss = True;	
      else
	pong_clock->bata_to_miss = True;
    }
  else
    {
      /* Reset the game */
      if (pong_clock->bata_to_miss)
	{
	  pong_clock->bata_to_miss = False;
	  pong_clock->ball_y = pong_clock->bata_y; 
	  pong_clock->ball_x = pong_clock->pixelw; 
	  pong_clock->ball_dx *= -1;
	}

      if (pong_clock->batb_to_miss)
	{
	  pong_clock->batb_to_miss = False;
	  pong_clock->ball_y = pong_clock->batb_y; 
	  pong_clock->ball_x = pong_clock->xwin_width - pong_clock->pixelw;
	  pong_clock->ball_dx *= -1; 
	}
    }

  /* Keep bats on field and only move in not setup to miss */
  if (pong_clock->ball_y >= (3*pong_clock->pixelh)
      && pong_clock->ball_y <=  (pong_clock->xwin_height - (5*pong_clock->pixelh)))
  {
    if (!pong_clock->batb_to_miss) 
      pong_clock->batb_y = pong_clock->ball_y;

    if (!pong_clock->bata_to_miss)
      pong_clock->bata_y = pong_clock->ball_y;
  }
}

void
draw_frame (PongClock *pong_clock)
{
  update_state (pong_clock);

  /* Clear playfield */
  XSetForeground (pong_clock->xdpy,
		  pong_clock->xgc,
		  BlackPixel(pong_clock->xdpy, 
			     pong_clock->xscreen));

  XFillRectangle (pong_clock->xdpy,
		  pong_clock->backbuffer,
		  pong_clock->xgc,
		  0, 0,
		  pong_clock->xwin_width, 
		  pong_clock->xwin_height);

  XSetForeground (pong_clock->xdpy,
		  pong_clock->xgc,
		  WhitePixel(pong_clock->xdpy, 
			     pong_clock->xscreen));

  draw_field (pong_clock);

  draw_time (pong_clock);

  draw_bat_and_ball (pong_clock);

  /* flip 'backbuffer' */
  XSetWindowBackgroundPixmap (pong_clock->xdpy, 
			      pong_clock->xwin,
			      pong_clock->backbuffer);
  XClearWindow(pong_clock->xdpy, pong_clock->xwin);

  XSync(pong_clock->xdpy, False);
}

int
main (int argc, char **argv)
{
  XGCValues  gcv;
  Atom       atoms_WINDOW_STATE, atoms_WINDOW_STATE_FULLSCREEN;
  PongClock *pong_clock;

  pong_clock = malloc(sizeof(PongClock));
  memset(pong_clock, 0, sizeof(PongClock));

  if ((pong_clock->xdpy = XOpenDisplay(getenv("DISPLAY"))) == NULL) {
    fprintf(stderr, "Cannot connect to X server on display %s.",
	    getenv("DISPLAY"));
    exit(-1);
  }

  pong_clock->xscreen     = DefaultScreen(pong_clock->xdpy);
  pong_clock->xwin_root   = DefaultRootWindow(pong_clock->xdpy);
  pong_clock->xwin_width  = DisplayWidth(pong_clock->xdpy, 
					 pong_clock->xscreen);
  pong_clock->xwin_height = DisplayHeight(pong_clock->xdpy, 
					  pong_clock->xscreen);

  pong_clock->pixelw  = pong_clock->xwin_width  / RESX;
  pong_clock->pixelh  = pong_clock->xwin_height / RESY;

  pong_clock->ball_x = 0; 
  pong_clock->ball_y = pong_clock->xwin_height / 2; 

  pong_clock->ball_dx = BALLDX; 
  pong_clock->ball_dy = BALLDY; 

  pong_clock->batb_y = pong_clock->bata_y = pong_clock->ball_y;

  gcv.background = BlackPixel(pong_clock->xdpy, 
			      pong_clock->xscreen);
  gcv.foreground = WhitePixel(pong_clock->xdpy, 
			      pong_clock->xscreen);
  gcv.graphics_exposures = False;

  pong_clock->xgc = XCreateGC (pong_clock->xdpy, pong_clock->xwin_root, 
			       GCForeground|GCBackground|GCGraphicsExposures,
			       &gcv);

  atoms_WINDOW_STATE
    = XInternAtom(pong_clock->xdpy, "_NET_WM_STATE",False);
  atoms_WINDOW_STATE_FULLSCREEN
    = XInternAtom(pong_clock->xdpy, "_NET_WM_STATE_FULLSCREEN",False);

  pong_clock->xwin = XCreateSimpleWindow(pong_clock->xdpy, 
					 pong_clock->xwin_root, 
					 0, 0,
					 pong_clock->xwin_width, 
					 pong_clock->xwin_height, 
					 0,
					 WhitePixel(pong_clock->xdpy, 
						    pong_clock->xscreen),
					 BlackPixel(pong_clock->xdpy, 
						    pong_clock->xscreen));

  pong_clock->backbuffer = XCreatePixmap(pong_clock->xdpy, 
					 pong_clock->xwin_root,
					 pong_clock->xwin_width, 
					 pong_clock->xwin_height, 
					 DefaultDepth(pong_clock->xdpy, 
						      pong_clock->xscreen));

  XSelectInput(pong_clock->xdpy, pong_clock->xwin, KeyPressMask);
  

  /* Set the hints for fullscreen */
  XChangeProperty(pong_clock->xdpy, 
		  pong_clock->xwin, 
		  atoms_WINDOW_STATE, 
		  XA_ATOM, 
		  32, 
		  PropModeReplace, 
		  (unsigned char *) &atoms_WINDOW_STATE_FULLSCREEN, 1);

  XMapWindow(pong_clock->xdpy, pong_clock->xwin);
  
  while (True) 
    {
      struct timeval timeout;
      XEvent         xev;

      timeout.tv_sec  = 0;
      timeout.tv_usec = 1000000 / FPS; 
      select (0, NULL, NULL, NULL, &timeout);

      draw_frame (pong_clock);

      XFlush(pong_clock->xdpy);

      if (XPending(pong_clock->xdpy))
	{
	  if (XCheckMaskEvent(pong_clock->xdpy, 
			      KeyPressMask,
			      &xev))
	    exit(-1);
	}
    }
}
