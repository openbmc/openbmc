/*
  gcc-6 busspeed.c cpuidc.c -lm -lrt -O3 -march=armv8-a -o busSpdPi64
  #define version "armv8 64 Bit"

  */

 #include <stdio.h>
 #include <stdlib.h>
 #include <string.h>
 #include <math.h>
 #include <inttypes.h>
 #include <stdint.h>
 #include "cpuidh.h"


//  ************** PROTOPTYPES *****************


void runAll();

void inc32words();
void inc16words();
void inc8words();
void inc4words();
void inc2words();
void inc1word();

//  #define version  "32 Bit V1.1"
//  #define version  "vfpv4 32b V1"
  #define version "armv8 64 Bit"
 
 int          array[16779000]; // 64 MB + 1000
 int          andsum1;
 int          andsumx = 0x55555555;
 unsigned int wordBytes = 4;

int             memoryWords;
unsigned int    memoryKBytes[20];
double          memoryMB;

int  mbpersec[20];

volatile int testToRun;
volatile int wordsToTest;
volatile int passes1;
int ipass;

double runSecs = 0.1; // 0.1
char resultchars[1000];


  void checkTime()
  {
     if (secs < runSecs)
     {
          if (secs < runSecs / 8.0)
          {
                passes1 = passes1 * 10;
          }
          else
          {
                passes1 = (int)(runSecs * 1.25 / secs * (double)passes1+1);
          }
      }
  }


int main(int argc, char *argv[])
{
    FILE    *outfile;
    int g;
    
    outfile = fopen("busSpeed.txt","a+");
    if (outfile == NULL)
    {
        printf ("Cannot open results file \n\n");
        printf(" Press Enter\n");
        g  = getchar();
        exit (0);
    }
    printf("\n");
    getDetails();
 
    printf(" ##########################################\n"); 
    fprintf (outfile, " #####################################################\n\n");                     

    printf ("\nFrom File /proc/cpuinfo\n");
    printf("%s\n", configdata[0]);
    printf ("\nFrom File /proc/version\n");
    printf("%s\n", configdata[1]);

    local_time();

    printf("    BusSpeed %s %s ", version, timeday);
    printf("      Copyright (C) 2013, Roy Longbottom\n\n");
    printf("    Reading Speed 4 Byte Words in MBytes/Second\n");
    printf("  Memory  Inc32  Inc16   Inc8   Inc4   Inc2   Read\n");
    printf("  KBytes  Words  Words  Words  Words  Words    All\n\n");

    fprintf(outfile, "   BusSpeed %s %s ", version, timeday);
    fprintf(outfile, "\n    Reading Speed 4 Byte Words in MBytes/Second\n");
    fprintf(outfile, "  Memory  Inc32  Inc16   Inc8   Inc4   Inc2   Read\n");
    fprintf(outfile, "  KBytes  Words  Words  Words  Words  Words    All\n\n");

    for (ipass=0; ipass<10; ipass++)
    {
        calcPass();
        printf("%s", resultchars); 
        fprintf(outfile, "%s", resultchars); 
    }
    local_time();
    fprintf(outfile, "\n        End of test %s", timeday);

     fprintf (outfile, "\nSYSTEM INFORMATION\n\nFrom File /proc/cpuinfo\n");
    fprintf (outfile, "%s \n", configdata[0]);
    fprintf (outfile, "\nFrom File /proc/version\n");
    fprintf (outfile, "%s \n", configdata[1]);
    fprintf (outfile, "\n");
    fflush(outfile);

    char moredata[1024];
    printf("\n Type additional information to include in busSpeed.txt - Press Enter\n");
    if (fgets (moredata, sizeof(moredata), stdin) != NULL)
             fprintf (outfile, "Additional information - %s\n", moredata);

    fflush(outfile);
    fclose(outfile);
           
    return 1;
}

int calcPass()
{
   int i, g, p;
   int j, tt0;
   int endTest;
   
   double addressInc[10];

   addressInc[0] = 32; // inc32words
   addressInc[1] = 16; // inc16words
   addressInc[2] = 8;  // inc8words
   addressInc[3] = 4;  // inc4words
   addressInc[4] = 2;  // inc2words
   addressInc[5] = 1;  // inc1word
   
   memoryKBytes[0] = 16;
   memoryKBytes[1] = 32;
   memoryKBytes[2] = 64;
   memoryKBytes[3] = 128;
   memoryKBytes[4] = 256;
   memoryKBytes[5] = 512;
   memoryKBytes[6] = 1024;
   memoryKBytes[7] = 4096;
   memoryKBytes[8] = 16384;
   memoryKBytes[9] = 65536;

   for (i=0; i<20; i++)
   {
       mbpersec[i] = 0;
   }
   memoryWords  = (int)((double)memoryKBytes[ipass] * 1024.0 / (double)wordBytes) ;
   memoryMB = ((double)memoryKBytes[ipass] * 1024.0)/ 1000000;        

   andsum1 = 0x55555555;
   for (i=0; i<memoryWords+1000; i++) 
   {
       array[i] = andsum1;
   }

   

   endTest = 7;
   

   wordsToTest = memoryWords;

   for (testToRun=1; testToRun<endTest; testToRun++)
   {
       tt0 = testToRun - 1;
       passes1 = 1;

        do
        {
           start_time();
           runAll();
           end_time();
           checkTime();
        }
        while (secs < runSecs);

       
       mbpersec[tt0] = (int)((double)passes1 * memoryMB / secs / addressInc[tt0]);
   }
 
   if (andsum1 != andsumx)
   {
       sprintf(resultchars, " Wrong result Expected 0x%8.8x Was 0x%8.8x at %d KB\n", andsumx, andsum1, memoryKBytes[ipass]);
       andsum1 = andsumx;
   }
   else
   {
         sprintf(resultchars, "%8d%7d%7d%7d%7d%7d%7d\n",        
                           memoryKBytes[ipass],
                           mbpersec[0], mbpersec[1],
                           mbpersec[2], mbpersec[3],
                           mbpersec[4], mbpersec[5]);
   }

   return 1;

} // runAllTests



void runAll()
{
    if (testToRun == 1) inc32words();
    if (testToRun == 2) inc16words();
    if (testToRun == 3) inc8words();
    if (testToRun == 4) inc4words();
    if (testToRun == 5) inc2words();
    if (testToRun == 6) inc1word();
    return;
}


void inc32words()
{
   int i, j;

   for(j=0; j<passes1; j++)
   {
       for (i=0; i<wordsToTest; i=i+768)
       {
           andsum1 = andsum1 & array[i     ] & array[i+32  ] & array[i+64  ] & array[i+96  ]
                             & array[i+128 ] & array[i+160 ] & array[i+192 ] & array[i+224 ]
                             & array[i+256 ] & array[i+288 ] & array[i+320 ] & array[i+352 ]
                             & array[i+384 ] & array[i+416 ] & array[i+448 ] & array[i+480 ]
                             & array[i+512 ] & array[i+544 ] & array[i+576 ] & array[i+608 ]
                             & array[i+640 ] & array[i+672 ] & array[i+704 ] & array[i+736 ];
       }
   }
}

void inc16words()
{
   int i, j;

   for(j=0; j<passes1; j++)
   {
       for (i=0; i<wordsToTest; i=i+768)
       {
           andsum1 = andsum1 & array[i    ] & array[i+16 ] & array[i+32 ] & array[i+48 ]
                             & array[i+64 ] & array[i+80 ] & array[i+96 ] & array[i+112]
                             & array[i+128] & array[i+144] & array[i+160] & array[i+176]
                             & array[i+192] & array[i+208] & array[i+224] & array[i+240]
                             & array[i+256] & array[i+272] & array[i+288] & array[i+304]
                             & array[i+320] & array[i+336] & array[i+352] & array[i+368]
                             & array[i+384] & array[i+400] & array[i+416] & array[i+432]
                             & array[i+448] & array[i+464] & array[i+480] & array[i+496]
                             & array[i+512] & array[i+528] & array[i+544] & array[i+560]
                             & array[i+576] & array[i+592] & array[i+608] & array[i+624]
                             & array[i+640] & array[i+656] & array[i+672] & array[i+688]
                             & array[i+704] & array[i+720] & array[i+736] & array[i+752];
       }
   }
}

void inc8words()
{
   int i, j;

   for(j=0; j<passes1; j++)
   {
       for (i=0; i<wordsToTest; i=i+384)
       {
           andsum1 = andsum1 & array[i    ] & array[i+8  ] & array[i+16 ] & array[i+24 ]
                             & array[i+32 ] & array[i+40 ] & array[i+48 ] & array[i+56 ]
                             & array[i+64 ] & array[i+72 ] & array[i+80 ] & array[i+88 ]
                             & array[i+96 ] & array[i+104] & array[i+112] & array[i+120]
                             & array[i+128] & array[i+136] & array[i+144] & array[i+152]
                             & array[i+160] & array[i+168] & array[i+176] & array[i+184]
                             & array[i+192] & array[i+200] & array[i+208] & array[i+216]
                             & array[i+224] & array[i+232] & array[i+240] & array[i+248]
                             & array[i+256] & array[i+264] & array[i+272] & array[i+280]
                             & array[i+288] & array[i+296] & array[i+304] & array[i+312]
                             & array[i+320] & array[i+328] & array[i+336] & array[i+344]
                             & array[i+352] & array[i+360] & array[i+368] & array[i+376];
       }
   }
}

void inc4words()
{
   int i, j;

   for(j=0; j<passes1; j++)
   {
       for (i=0; i<wordsToTest; i=i+256)
       {
           andsum1 = andsum1 & array[i    ] & array[i+4  ] & array[i+8  ] & array[i+12 ]
                             & array[i+16 ] & array[i+20 ] & array[i+24 ] & array[i+28 ]
                             & array[i+32 ] & array[i+36 ] & array[i+40 ] & array[i+44 ]
                             & array[i+48 ] & array[i+52 ] & array[i+56 ] & array[i+60 ]
                             & array[i+64 ] & array[i+68 ] & array[i+72 ] & array[i+76 ]
                             & array[i+80 ] & array[i+84 ] & array[i+88 ] & array[i+92 ]
                             & array[i+96 ] & array[i+100] & array[i+104] & array[i+108]
                             & array[i+112] & array[i+116] & array[i+120] & array[i+124]
                             & array[i+128] & array[i+132] & array[i+136] & array[i+140]
                             & array[i+144] & array[i+148] & array[i+152] & array[i+156]
                             & array[i+160] & array[i+164] & array[i+168] & array[i+172]
                             & array[i+176] & array[i+180] & array[i+184] & array[i+188]
                             & array[i+192] & array[i+196] & array[i+200] & array[i+204]
                             & array[i+208] & array[i+212] & array[i+216] & array[i+220]
                             & array[i+224] & array[i+228] & array[i+232] & array[i+236]
                             & array[i+240] & array[i+244] & array[i+248] & array[i+252];
       }
   }
}

void inc2words()
{
   int i, j;

   for(j=0; j<passes1; j++)
   {
       for (i=0; i<wordsToTest; i=i+128)
       {
           andsum1 = andsum1 & array[i    ] & array[i+2  ] & array[i+4  ] & array[i+6  ]
                             & array[i+8  ] & array[i+10 ] & array[i+12 ] & array[i+14 ]
                             & array[i+16 ] & array[i+18 ] & array[i+20 ] & array[i+22 ]
                             & array[i+24 ] & array[i+26 ] & array[i+28 ] & array[i+30 ]
                             & array[i+32 ] & array[i+34 ] & array[i+36 ] & array[i+38 ]
                             & array[i+40 ] & array[i+42 ] & array[i+44 ] & array[i+46 ]
                             & array[i+48 ] & array[i+50 ] & array[i+52 ] & array[i+54 ]
                             & array[i+56 ] & array[i+58 ] & array[i+60 ] & array[i+62 ]
                             & array[i+64 ] & array[i+66 ] & array[i+68 ] & array[i+70 ]
                             & array[i+72 ] & array[i+74 ] & array[i+76 ] & array[i+78 ]
                             & array[i+80 ] & array[i+82 ] & array[i+84 ] & array[i+86 ]
                             & array[i+88 ] & array[i+90 ] & array[i+92 ] & array[i+94 ]
                             & array[i+96 ] & array[i+98 ] & array[i+100] & array[i+102]
                             & array[i+104] & array[i+106] & array[i+108] & array[i+110]
                             & array[i+112] & array[i+114] & array[i+116] & array[i+118]
                             & array[i+120] & array[i+122] & array[i+124] & array[i+126];
       }
    }
}

void inc1word()
{
   int i, j;

   for(j=0; j<passes1; j++)
   {
       for (i=0; i<wordsToTest; i=i+64)
       {
           andsum1 = andsum1 & array[i   ] & array[i+1 ] & array[i+2 ] & array[i+3 ]
                             & array[i+4 ] & array[i+5 ] & array[i+6 ] & array[i+7 ]
                             & array[i+8 ] & array[i+9 ] & array[i+10] & array[i+11]
                             & array[i+12] & array[i+13] & array[i+14] & array[i+15]
                             & array[i+16] & array[i+17] & array[i+18] & array[i+19]
                             & array[i+20] & array[i+21] & array[i+22] & array[i+23]
                             & array[i+24] & array[i+25] & array[i+26] & array[i+27]
                             & array[i+28] & array[i+29] & array[i+30] & array[i+31]
                             & array[i+32] & array[i+33] & array[i+34] & array[i+35]
                             & array[i+36] & array[i+37] & array[i+38] & array[i+39]
                             & array[i+40] & array[i+41] & array[i+42] & array[i+43]
                             & array[i+44] & array[i+45] & array[i+46] & array[i+47]
                             & array[i+48] & array[i+49] & array[i+50] & array[i+51]
                             & array[i+52] & array[i+53] & array[i+54] & array[i+55]
                             & array[i+56] & array[i+57] & array[i+58] & array[i+59]
                             & array[i+60] & array[i+61] & array[i+62] & array[i+63];
       }
   }
}



