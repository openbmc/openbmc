/*
 gcc-6 fft3c.c cpuidc.c -lm -lrt -O3 -march=armv8-a  -o fft3c-RPi64 
 version armv8 64 Bit FFT Benchmark Version 3c.0
*/


 #include <stdio.h>       
 #include <stdlib.h>
 #include <math.h>
 #include <time.h>
 #include "cpuidh.h"

// ************* GLOBAL VARIABLES **********



#define TsizeL2     13  // cache size parameter 13 = 8K FFT 128 KB SP 256
                        // KB DP when segmenting data to fit in L2 cache
                        // for fftSP() and fftDP()
#define TsizeLL     14  // cache size parameter TsizeL2 + 1

double   cDataDP[8388608]; // 8 MW 64 MB;
int      RAMunit = 1048576;

double   *cSourceDP;
double   *cExpDP;
double   *dExpDP;   
double   *dTable;

float    *cDataSP;
float    *cSourceSP;
float    *cExpSP;
float    *dExpSP;
int      *table;  

float    *pData;
double   *pDatad;

double  ExpReal1DP;
double  ExpImag1DP;    

double  ExpReal2DP;
double  ExpImag2DP;
double  ExpReal3DP;
double  ExpImag3DP;
double  ExpReal4DP;
double  ExpImag4DP;
float  ExpReal1SP;
float  ExpImag1SP;
float  ExpReal2SP;
float  ExpImag2SP;
float  ExpReal3SP;
float  ExpImag3SP;
float  ExpReal4SP;
float  ExpImag4SP;
 

 float  PISP = (float)3.14159265;
 double PIDP = 3.14159265358979323846;

 int  nSP;
 int  nDP;


 char    checkSP[80];
 char    checkDP[80];
 char    resultchars[1200];
 
//  ************** PROTOPTYPES *****************

void bitrev(int);
void createExpTableSP(int n, int sign);
void createExpTableDP(int n, int sign);
void createComplexDataDP (int n, double synthFreqI, double synthFreqQ);
void createComplexDataSP (int n, float synthFreqI, float synthFreqQ);

void fftSP(int t, int n); 
void fftDP(int t, int n);
void fftSP2(int t,int n); 
void fftDP2(int t, int n);

void checkNoiseSP();
void checkNoiseDP();


void runIt();


void main()
{
    int i, g;
    char version[80] = " armv8 64 Bit FFT Benchmark Version 3c.0 ";
    FILE    *outfile;
    
    outfile = fopen("FFT-tests.txt","a+");
    if (outfile == NULL)
    {
        printf ("Cannot open results file \n\n");
        printf(" Press Enter\n");
        g  = getchar();
        exit (0);
    }
    getDetails();
    local_time();
    fprintf(outfile, " ###################################################\n\n");

    printf ("\nFrom File /proc/cpuinfo\n");
    printf("%s\n", configdata[0]);
    printf ("\nFrom File /proc/version\n");
    printf("%s\n", configdata[1]);

    fprintf (outfile, "%s \n", configdata[0]);
    fprintf (outfile, "\nFrom File /proc/version\n");
    fprintf (outfile, "%s \n", configdata[1]);

    printf("\n");
    printf(" ###################################################\n\n");
    printf("  Raspberry Pi 3 Running time > 15 seconds before results display\n\n"); 
    printf("  %s%s\n", version, timeday);

    fprintf(outfile, "\n");
    fprintf(outfile, " ###################################################\n\n");
    fprintf(outfile, "  %s%s\n", version, timeday);

    runIt();

    printf("%s\n", resultchars);
    fprintf(outfile, "%s\n", resultchars);
    fclose(outfile);
}


void runIt()
{
  int j, k, n, t, sign;
  float synthFreq;
  int size[11];
  double mt[11][6];

   cSourceDP  = cDataDP + RAMunit*2;
   cExpDP     = cDataDP + RAMunit*4;
   dExpDP     = cDataDP + RAMunit*5;
   dTable     = cDataDP + RAMunit*6;

   cDataSP    = (float  *)cDataDP;
   cSourceSP  = (float  *)cSourceDP;
   cExpSP     = (float  *)cExpDP;
   dExpSP     = (float  *)dExpDP;
   table      = (int    *)dTable;

  for (t=10; t<21; t++)
  {
    k = t-10;
    n = (int)pow(2,(double)t);
    nSP = n; 
    size[k] = n/1024;  
    bitrev (t); 
    sign = -1; 
    createExpTableSP(n, sign);
    synthFreq = (float)n/16;

    for(j=0;j<3;j++)
    {
        createComplexDataSP(n, synthFreq, synthFreq);
        start_time(); 
        if (t < TsizeLL)
        {    
           fftSP2 (t, n);
        }
        else
        {
           fftSP (t, n);
        } 
        end_time();
        mt[k][j] = secs * 1000.0;
        checkNoiseSP();       
    }
  }   

  for (t=10; t<21; t++)
  {
    k = t-10;
    n = (int)pow(2,(double)t); 
    nDP = n;
    size[k] = n/1024;  
    bitrev (t); 
    sign = -1; 
    createExpTableDP(n, sign);
    synthFreq = (float)n/16;
    
    for(j=0;j<3;j++)
    {
        createComplexDataDP(n, synthFreq, synthFreq);
        start_time(); 
        if (t < TsizeLL)
        {    
           fftDP2 (t, n); 
        }
        else
        {
           fftDP (t, n); 
        }            
        end_time();
        mt[k][j+3] = secs * 1000;
        checkNoiseDP();
    }
  }

 
  local_time();
  sprintf(resultchars, "  Size                     milliseconds\n"
                       "    K     Single Precision              Double Precision\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n"
                       "%5d%10.3f%10.3f%10.3f%10.3f%10.3f%10.3f\n\n"
                       "        1024 Square Check Maximum Noise Average Noise\n"
                       "       %s\n       %s\n\n               End at %s",
              size[ 0], mt[ 0][0], mt[ 0][1], mt[ 0][2], mt[ 0][3], mt[ 0][4], mt[ 0][5], 
              size[ 1], mt[ 1][0], mt[ 1][1], mt[ 1][2], mt[ 1][3], mt[ 1][4], mt[ 1][5], 
              size[ 2], mt[ 2][0], mt[ 2][1], mt[ 2][2], mt[ 2][3], mt[ 2][4], mt[ 2][5], 
              size[ 3], mt[ 3][0], mt[ 3][1], mt[ 3][2], mt[ 3][3], mt[ 3][4], mt[ 3][5], 
              size[ 4], mt[ 4][0], mt[ 4][1], mt[ 4][2], mt[ 4][3], mt[ 4][4], mt[ 4][5], 
              size[ 5], mt[ 5][0], mt[ 5][1], mt[ 5][2], mt[ 5][3], mt[ 5][4], mt[ 5][5], 
              size[ 6], mt[ 6][0], mt[ 6][1], mt[ 6][2], mt[ 6][3], mt[ 6][4], mt[ 6][5], 
              size[ 7], mt[ 7][0], mt[ 7][1], mt[ 7][2], mt[ 7][3], mt[ 7][4], mt[ 7][5], 
              size[ 8], mt[ 8][0], mt[ 8][1], mt[ 8][2], mt[ 8][3], mt[ 8][4], mt[ 8][5], 
              size[ 9], mt[ 9][0], mt[ 9][1], mt[ 9][2], mt[ 9][3], mt[ 9][4], mt[ 9][5], 
              size[10], mt[10][0], mt[10][1], mt[10][2], mt[10][3], mt[10][4], mt[10][5],
              checkSP, checkDP, timeday);  
}


void fftSP2 (int t, int n)
{
  float  inverse_n;
  int   d, d2;
  int   index;
  int   point;
  int   j; 
  int   jinc;
  float  temp1, temp2;
  int k, jk, jkd;
  float  tempReal, tempImag;

  inverse_n = (float)1.0 / (float)n;
  
    for (index=0; index<n; index++)
    {
            point = table[index];
            if (point >= index)
            {
              temp1  = cSourceSP[index*2]     * inverse_n;
              temp2  = cSourceSP[(index*2)+1] * inverse_n;
              cSourceSP[index*2]     = cSourceSP[point*2]     * inverse_n;
              cSourceSP[(index*2)+1] = cSourceSP[(point*2)+1] * inverse_n;
              cSourceSP[point*2]     = temp1;
              cSourceSP[(point*2)+1] = temp2;
            }
            
    }

    d = 1;
    for(index=1;index<=t;index++)
    {
        d2 = d + d;
        jinc = 2;
        if (d > 2) jinc = 4;
        for (j=0; j<d; j=j+jinc)
        {

            ExpReal1SP = cExpSP[(n / d2) * j * 2];
            ExpImag1SP = cExpSP[(n / d2) * j * 2+1];
            ExpReal2SP = cExpSP[(n / d2) * (j+1)*2];
            ExpImag2SP = cExpSP[(n / d2) * (j+1)*2+1];

            if (d > 2)
            { 
                ExpReal3SP = cExpSP[(n / d2) * (j+2)*2];
                ExpImag3SP = cExpSP[(n / d2) * (j+2)*2+1];    
                ExpReal4SP = cExpSP[(n / d2) * (j+3)*2];
                ExpImag4SP = cExpSP[(n / d2) * (j+3)*2+1];
            }

            if (d == 1)
            {
                for(k=0;k<n;k=k+d2)
                {
                      jk           = (j + k) * 2;
                      jkd          = (j + k + d) * 2;
                      tempReal   = (cDataSP[jkd]   * ExpReal1SP) - (cDataSP[jkd+1] * ExpImag1SP);
                      tempImag   = (cDataSP[jkd+1] * ExpReal1SP) + (cDataSP[jkd]   * ExpImag1SP);
                      cDataSP[jkd]  = cDataSP[jk] - tempReal;
                      cDataSP[jkd+1]  = cDataSP[jk+1] - tempImag;
                      cDataSP[jk]   = cDataSP[jk] + tempReal;
                      cDataSP[jk+1]   = cDataSP[jk+1] + tempImag;
                }
            }
            else if (d == 2)
            {
                for(k=0;k<n;k=k+d2)
                {
                      jk           = (j + k) * 2;
                      jkd          = (j + k + d) * 2;
                      tempReal   = (cDataSP[jkd]   * ExpReal1SP) - (cDataSP[jkd+1] * ExpImag1SP);
                      tempImag   = (cDataSP[jkd+1] * ExpReal1SP) + (cDataSP[jkd]   * ExpImag1SP);
                      cDataSP[jkd]  = cDataSP[jk] - tempReal;
                      cDataSP[jkd+1]  = cDataSP[jk+1] - tempImag;
                      cDataSP[jk]   = cDataSP[jk] + tempReal;
                      cDataSP[jk+1]   = cDataSP[jk+1] + tempImag;
        
                      tempReal   = (cDataSP[jkd+2] * ExpReal2SP) - (cDataSP[jkd+3] * ExpImag2SP);
                      tempImag   = (cDataSP[jkd+3] * ExpReal2SP) + (cDataSP[jkd+2] * ExpImag2SP);
                      cDataSP[jkd+2]  = cDataSP[jk+2] - tempReal;
                      cDataSP[jkd+3]  = cDataSP[jk+3] - tempImag;
                      cDataSP[jk+2]   = cDataSP[jk+2] + tempReal;
                      cDataSP[jk+3]   = cDataSP[jk+3] + tempImag;        
                }
            }
            else
            {
                for(k=0;k<n;k=k+d2)
                {
                      jk           = (j + k) * 2;
                      jkd          = (j + k + d) * 2;
                      tempReal   = (cDataSP[jkd]   * ExpReal1SP) - (cDataSP[jkd+1] * ExpImag1SP);
                      tempImag   = (cDataSP[jkd+1] * ExpReal1SP) + (cDataSP[jkd]   * ExpImag1SP);
                      cDataSP[jkd]  = cDataSP[jk] - tempReal;
                      cDataSP[jkd+1]  = cDataSP[jk+1] - tempImag;
                      cDataSP[jk]   = cDataSP[jk] + tempReal;
                      cDataSP[jk+1]   = cDataSP[jk+1] + tempImag;
        
                      tempReal   = (cDataSP[jkd+2] * ExpReal2SP) - (cDataSP[jkd+3] * ExpImag2SP);
                      tempImag   = (cDataSP[jkd+3] * ExpReal2SP) + (cDataSP[jkd+2] * ExpImag2SP);
                      cDataSP[jkd+2]  = cDataSP[jk+2] - tempReal;
                      cDataSP[jkd+3]  = cDataSP[jk+3] - tempImag;
                      cDataSP[jk+2]   = cDataSP[jk+2] + tempReal;
                      cDataSP[jk+3]   = cDataSP[jk+3] + tempImag;
        
                      tempReal   = (cDataSP[jkd+4] * ExpReal3SP) - (cDataSP[jkd+5] * ExpImag3SP);
                      tempImag   = (cDataSP[jkd+5] * ExpReal3SP) + (cDataSP[jkd+4] * ExpImag3SP);
                      cDataSP[jkd+4]  = cDataSP[jk+4] - tempReal;
                      cDataSP[jkd+5]  = cDataSP[jk+5] - tempImag;
                      cDataSP[jk+4]   = cDataSP[jk+4] + tempReal;
                      cDataSP[jk+5]   = cDataSP[jk+5] + tempImag;
        
                      tempReal   = (cDataSP[jkd+6] * ExpReal4SP) - (cDataSP[jkd+7] * ExpImag4SP);
                      tempImag   = (cDataSP[jkd+7] * ExpReal4SP) + (cDataSP[jkd+6] * ExpImag4SP);
                      cDataSP[jkd+6]  = cDataSP[jk+6] - tempReal;
                      cDataSP[jkd+7]  = cDataSP[jk+7] - tempImag;
                      cDataSP[jk+6]   = cDataSP[jk+6] + tempReal;
                      cDataSP[jk+7]   = cDataSP[jk+7] + tempImag;
                }
            }
        }
        d = d2;
    }
} // fftSP2

void fftSP (int t, int n)
{
    float  inverse_n;
    int   index;
    int   d = 0;
    int   d2;
    int   point, Nsize, Tsize, seg, NumSeg, offset, RevSeg;
    int   j;
    int  k, jinc;
    int   jk, jkd;
    float  tempReal, tempImag;

    inverse_n = (float)1.0 / (float)n;

    if(t >= TsizeL2)
       Tsize = TsizeL2;
    else
        Tsize = t;

    Nsize  = (int)pow((double)2,(double)Tsize);
    NumSeg = (int) n / Nsize;

    //************************************************************
    // Break the work up into NumSeg work units of size TsizeL2 
    //************************************************************

    for(seg=0;seg<NumSeg;seg++)
    {
        RevSeg = 0;
        for(k=0;k<(t-Tsize);k++)
                RevSeg = (RevSeg << 1) | ((seg >> k) & 0x1);

        pData = cDataSP+(RevSeg*Nsize*2);
        offset = seg * 2;

        // bit reverse each set
        for (index=0; index<Nsize; index++)
        {
            point = table[index];
            if (point >= index)
            {
                pData[index*2]     = cSourceSP[(point*2*NumSeg)+offset]   * inverse_n;
                pData[(index*2)+1] = cSourceSP[(point*2*NumSeg)+offset+1] * inverse_n;       
                pData[point*2]     = cSourceSP[(index*2*NumSeg)+offset]   * inverse_n;
                pData[(point*2)+1] = cSourceSP[(index*2*NumSeg)+offset+1] * inverse_n;
            }
        }

        // transform each set
        d = 1;
        for(index=1;index<=Tsize;index++)
        {
            d2 = d + d;
            jinc = 2;
            if (d > 2) jinc = 4;
            for (j=0; j<d; j=j+jinc)
            {

                ExpReal1SP = dExpSP[(Nsize / d2) * j * 2];
                ExpImag1SP = dExpSP[(Nsize / d2) * j * 2+1];
                ExpReal2SP = dExpSP[(Nsize / d2) * (j+1)*2];
                ExpImag2SP = dExpSP[(Nsize / d2) * (j+1)*2+1];

                if (d > 2)
                { 
                    ExpReal3SP = dExpSP[(Nsize / d2) * (j+2)*2];
                    ExpImag3SP = dExpSP[(Nsize / d2) * (j+2)*2+1];    
                    ExpReal4SP = dExpSP[(Nsize / d2) * (j+3)*2];
                    ExpImag4SP = dExpSP[(Nsize / d2) * (j+3)*2+1];
                }

                if (d == 1)
                {
                    for(k=0;k<Nsize;k=k+d2)
                    {
                      jk      = (j + k) * 2;
                      jkd = (j + k + d) * 2;
                      tempReal   = (pData[jkd]   * ExpReal1SP) - (pData[jkd+1] * ExpImag1SP);
                      tempImag   = (pData[jkd+1] * ExpReal1SP) + (pData[jkd]   * ExpImag1SP);
                      pData[jkd]  = pData[jk] - tempReal;
                      pData[jkd+1]  = pData[jk+1] - tempImag;
                      pData[jk]   = pData[jk] + tempReal;
                      pData[jk+1]   = pData[jk+1] + tempImag;
                    }
                }
                else if (d == 2)
                {
                    for(k=0;k<Nsize;k=k+d2)
                    {
                      jk      = (j + k) * 2;
                      jkd = (j + k + d) * 2;
                      tempReal   = (pData[jkd]   * ExpReal1SP) - (pData[jkd+1] * ExpImag1SP);
                      tempImag   = (pData[jkd+1] * ExpReal1SP) + (pData[jkd]   * ExpImag1SP);
                      pData[jkd]  = pData[jk] - tempReal;
                      pData[jkd+1]  = pData[jk+1] - tempImag;
                      pData[jk]   = pData[jk] + tempReal;
                      pData[jk+1]   = pData[jk+1] + tempImag;
        
                      tempReal   = (pData[jkd+2] * ExpReal2SP) - (pData[jkd+3] * ExpImag2SP);
                      tempImag   = (pData[jkd+3] * ExpReal2SP) + (pData[jkd+2] * ExpImag2SP);
                      pData[jkd+2]  = pData[jk+2] - tempReal;
                      pData[jkd+3]  = pData[jk+3] - tempImag;
                      pData[jk+2]   = pData[jk+2] + tempReal;
                      pData[jk+3]   = pData[jk+3] + tempImag;
                    }
                }
                else
                {
                    for(k=0;k<Nsize;k=k+d2)
                    {
                      jk      = (j + k) * 2;
                      jkd = (j + k + d) * 2;
                      tempReal   = (pData[jkd]   * ExpReal1SP) - (pData[jkd+1] * ExpImag1SP);
                      tempImag   = (pData[jkd+1] * ExpReal1SP) + (pData[jkd]   * ExpImag1SP);
                      pData[jkd]  = pData[jk] - tempReal;
                      pData[jkd+1]  = pData[jk+1] - tempImag;
                      pData[jk]   = pData[jk] + tempReal;
                      pData[jk+1]   = pData[jk+1] + tempImag;
        
                      tempReal   = (pData[jkd+2] * ExpReal2SP) - (pData[jkd+3] * ExpImag2SP);
                      tempImag   = (pData[jkd+3] * ExpReal2SP) + (pData[jkd+2] * ExpImag2SP);
                      pData[jkd+2]  = pData[jk+2] - tempReal;
                      pData[jkd+3]  = pData[jk+3] - tempImag;
                      pData[jk+2]   = pData[jk+2] + tempReal;
                      pData[jk+3]   = pData[jk+3] + tempImag;
        
                      tempReal   = (pData[jkd+4] * ExpReal3SP) - (pData[jkd+5] * ExpImag3SP);
                      tempImag   = (pData[jkd+5] * ExpReal3SP) + (pData[jkd+4] * ExpImag3SP);
                      pData[jkd+4]  = pData[jk+4] - tempReal;
                      pData[jkd+5]  = pData[jk+5] - tempImag;
                      pData[jk+4]   = pData[jk+4] + tempReal;
                      pData[jk+5]   = pData[jk+5] + tempImag;
        
                      tempReal   = (pData[jkd+6] * ExpReal4SP) - (pData[jkd+7] * ExpImag4SP);
                      tempImag   = (pData[jkd+7] * ExpReal4SP) + (pData[jkd+6] * ExpImag4SP);
                      pData[jkd+6]  = pData[jk+6] - tempReal;
                      pData[jkd+7]  = pData[jk+7] - tempImag;
                      pData[jk+6]   = pData[jk+6] + tempReal;
                      pData[jk+7]   = pData[jk+7] + tempImag;
                    }
                }                            
            }
            d = d2;
        }
    }

    //***********************************************************
    // This will recombine the above work units (if necessary) 
    //  Only run when t > TsizeL2 eg TsizeL2 = 13 run at size 16384
    //**********************************************************

   for(index=TsizeL2+1; index<=t; index++)
   {
      d2 = d + d;
      for (j=0; j<d; j=j+4)
      {
        ExpReal1SP = cExpSP[(n / d2 * j)*2];
        ExpImag1SP = cExpSP[(n / d2 * j)*2+1];
        ExpReal2SP = cExpSP[(n / d2 * (j+1))*2];
        ExpImag2SP = cExpSP[(n / d2 * (j+1))*2+1];
        ExpReal3SP = cExpSP[(n / d2 * (j+2))*2];
        ExpImag3SP = cExpSP[(n / d2 * (j+2))*2+1];
        ExpReal4SP = cExpSP[(n / d2 * (j+3))*2];
        ExpImag4SP = cExpSP[(n / d2 * (j+3))*2+1];

        for(k=0;k<n;k=k+d2)
        {
          jk      = (j + k) * 2;
          jkd = (j + k + d) * 2;
          tempReal   = (cDataSP[jkd]   * ExpReal1SP) - (cDataSP[jkd+1] * ExpImag1SP);
          tempImag   = (cDataSP[jkd+1] * ExpReal1SP) + (cDataSP[jkd]   * ExpImag1SP);
          cDataSP[jkd]  = cDataSP[jk] - tempReal;
          cDataSP[jkd+1]  = cDataSP[jk+1] - tempImag;
          cDataSP[jk]   = cDataSP[jk] + tempReal;
          cDataSP[jk+1]   = cDataSP[jk+1] + tempImag;

          tempReal   = (cDataSP[jkd+2] * ExpReal2SP) - (cDataSP[jkd+3] * ExpImag2SP);
          tempImag   = (cDataSP[jkd+3] * ExpReal2SP) + (cDataSP[jkd+2] * ExpImag2SP);
          cDataSP[jkd+2]  = cDataSP[jk+2] - tempReal;
          cDataSP[jkd+3]  = cDataSP[jk+3] - tempImag;
          cDataSP[jk+2]   = cDataSP[jk+2] + tempReal;
          cDataSP[jk+3]   = cDataSP[jk+3] + tempImag;

          tempReal   = (cDataSP[jkd+4] * ExpReal3SP) - (cDataSP[jkd+5] * ExpImag3SP);
          tempImag   = (cDataSP[jkd+5] * ExpReal3SP) + (cDataSP[jkd+4] * ExpImag3SP);
          cDataSP[jkd+4]  = cDataSP[jk+4] - tempReal;
          cDataSP[jkd+5]  = cDataSP[jk+5] - tempImag;
          cDataSP[jk+4]   = cDataSP[jk+4] + tempReal;
          cDataSP[jk+5]   = cDataSP[jk+5] + tempImag;

          tempReal   = (cDataSP[jkd+6] * ExpReal4SP) - (cDataSP[jkd+7] * ExpImag4SP);
          tempImag   = (cDataSP[jkd+7] * ExpReal4SP) + (cDataSP[jkd+6] * ExpImag4SP);
          cDataSP[jkd+6]  = cDataSP[jk+6] - tempReal;
          cDataSP[jkd+7]  = cDataSP[jk+7] - tempImag;
          cDataSP[jk+6]   = cDataSP[jk+6] + tempReal;
          cDataSP[jk+7]   = cDataSP[jk+7] + tempImag;
        }
      }
      d = d2;
   }

} // fftSP


void fftDP2 (int t, int n)
{
  double  inverse_n;
  int   d, d2;
  int   index;
  int   point;
  int   j, k, jk, jkd; 
  int   jinc;
  double  temp1, temp2;
  double  tempReal, tempImag;

  inverse_n = 1.0 / (double)n;

    for (index=0; index<n; index++)
    {
            point = table[index];
            if (point >= index)
            {
              temp1  = cSourceDP[index*2] * inverse_n;
              temp2  = cSourceDP[(index*2)+1] * inverse_n;
              cSourceDP[index*2] = cSourceDP[point*2] * inverse_n;
              cSourceDP[(index*2)+1] = cSourceDP[(point*2)+1] * inverse_n;
              cSourceDP[point*2]     = temp1;
              cSourceDP[(point*2)+1] = temp2;
            }
    }

    d = 1;
    for(index=1;index<=t;index++)
    {
        d2 = d + d;
        jinc = 2;
        if (d > 2) jinc = 4;
        for (j=0; j<d; j=j+jinc)
        {

            ExpReal1DP = cExpDP[(n / d2) * j * 2];
            ExpImag1DP = cExpDP[(n / d2) * j * 2+1];
            ExpReal2DP = cExpDP[(n / d2) * (j+1)*2];
            ExpImag2DP = cExpDP[(n / d2) * (j+1)*2+1];

            if (d > 2)
            { 
                ExpReal3DP = cExpDP[(n / d2) * (j+2)*2];
                ExpImag3DP = cExpDP[(n / d2) * (j+2)*2+1];    
                ExpReal4DP = cExpDP[(n / d2) * (j+3)*2];
                ExpImag4DP = cExpDP[(n / d2) * (j+3)*2+1];
            }

            if (d == 1)
            {
                for(k=0;k<n;k=k+d2)
                {
                      jk           = (j + k) * 2;
                      jkd          = (j + k + d) * 2;
                      tempReal   = (cDataDP[jkd]   * ExpReal1DP) - (cDataDP[jkd+1] * ExpImag1DP);
                      tempImag   = (cDataDP[jkd+1] * ExpReal1DP) + (cDataDP[jkd]   * ExpImag1DP);
                      cDataDP[jkd]  = cDataDP[jk] - tempReal;
                      cDataDP[jkd+1]  = cDataDP[jk+1] - tempImag;
                      cDataDP[jk]   = cDataDP[jk] + tempReal;
                      cDataDP[jk+1]   = cDataDP[jk+1] + tempImag;
                }
            }
            else if (d == 2)
            {
                for(k=0;k<n;k=k+d2)
                {
                      jk           = (j + k) * 2;
                      jkd          = (j + k + d) * 2;
                      tempReal   = (cDataDP[jkd]   * ExpReal1DP) - (cDataDP[jkd+1] * ExpImag1DP);
                      tempImag   = (cDataDP[jkd+1] * ExpReal1DP) + (cDataDP[jkd]   * ExpImag1DP);
                      cDataDP[jkd]  = cDataDP[jk] - tempReal;
                      cDataDP[jkd+1]  = cDataDP[jk+1] - tempImag;
                      cDataDP[jk]   = cDataDP[jk] + tempReal;
                      cDataDP[jk+1]   = cDataDP[jk+1] + tempImag;
        
                      tempReal   = (cDataDP[jkd+2] * ExpReal2DP) - (cDataDP[jkd+3] * ExpImag2DP);
                      tempImag   = (cDataDP[jkd+3] * ExpReal2DP) + (cDataDP[jkd+2] * ExpImag2DP);
                      cDataDP[jkd+2]  = cDataDP[jk+2] - tempReal;
                      cDataDP[jkd+3]  = cDataDP[jk+3] - tempImag;
                      cDataDP[jk+2]   = cDataDP[jk+2] + tempReal;
                      cDataDP[jk+3]   = cDataDP[jk+3] + tempImag;        
                }
            }
            else
            {
                for(k=0;k<n;k=k+d2)
                {
                      jk           = (j + k) * 2;
                      jkd          = (j + k + d) * 2;
                      tempReal   = (cDataDP[jkd]   * ExpReal1DP) - (cDataDP[jkd+1] * ExpImag1DP);
                      tempImag   = (cDataDP[jkd+1] * ExpReal1DP) + (cDataDP[jkd]   * ExpImag1DP);
                      cDataDP[jkd]  = cDataDP[jk] - tempReal;
                      cDataDP[jkd+1]  = cDataDP[jk+1] - tempImag;
                      cDataDP[jk]   = cDataDP[jk] + tempReal;
                      cDataDP[jk+1]   = cDataDP[jk+1] + tempImag;
        
                      tempReal   = (cDataDP[jkd+2] * ExpReal2DP) - (cDataDP[jkd+3] * ExpImag2DP);
                      tempImag   = (cDataDP[jkd+3] * ExpReal2DP) + (cDataDP[jkd+2] * ExpImag2DP);
                      cDataDP[jkd+2]  = cDataDP[jk+2] - tempReal;
                      cDataDP[jkd+3]  = cDataDP[jk+3] - tempImag;
                      cDataDP[jk+2]   = cDataDP[jk+2] + tempReal;
                      cDataDP[jk+3]   = cDataDP[jk+3] + tempImag;
        
                      tempReal   = (cDataDP[jkd+4] * ExpReal3DP) - (cDataDP[jkd+5] * ExpImag3DP);
                      tempImag   = (cDataDP[jkd+5] * ExpReal3DP) + (cDataDP[jkd+4] * ExpImag3DP);
                      cDataDP[jkd+4]  = cDataDP[jk+4] - tempReal;
                      cDataDP[jkd+5]  = cDataDP[jk+5] - tempImag;
                      cDataDP[jk+4]   = cDataDP[jk+4] + tempReal;
                      cDataDP[jk+5]   = cDataDP[jk+5] + tempImag;
        
                      tempReal   = (cDataDP[jkd+6] * ExpReal4DP) - (cDataDP[jkd+7] * ExpImag4DP);
                      tempImag   = (cDataDP[jkd+7] * ExpReal4DP) + (cDataDP[jkd+6] * ExpImag4DP);
                      cDataDP[jkd+6]  = cDataDP[jk+6] - tempReal;
                      cDataDP[jkd+7]  = cDataDP[jk+7] - tempImag;
                      cDataDP[jk+6]   = cDataDP[jk+6] + tempReal;
                      cDataDP[jk+7]   = cDataDP[jk+7] + tempImag;
                }
            }
        }
        d = d2;
    }
} // fftDP2

void fftDP (int t, int n)
{
    double  inverse_n;
    
    int   d = 0;
    int   d2;
    int   index;
    int   point, Nsize, Tsize, seg, NumSeg, offset, RevSeg;
    int   offo2;
    int   j, k, jk, jkd, jinc;
    double  tempReal, tempImag;


    inverse_n = 1.0 / (double)n;

    if(t >= TsizeL2)
    {
       Tsize = TsizeL2;
    }
    else
    {
        Tsize = t;
    }
    Nsize  = (int)pow((double)2,(double)Tsize);
    NumSeg = n / Nsize;
    
    //************************************************************
    // Break the work up into NumSeg work units of size TsizeL2 
    //************************************************************

    for(seg=0;seg<NumSeg;seg++)
    {
        RevSeg = 0;
        for(k=0;k<(t-Tsize);k++)
                RevSeg = (RevSeg << 1) | ((seg >> k) & 0x1);

        pDatad = cDataDP+(RevSeg*Nsize*2);
        offset = seg * 2;
        offo2  = seg;

        // bit reverse each set
       
        for (index=0; index<Nsize; index++)
        {
            point = table[index];
            if (point >= index)
            {
                pDatad[index*2]     = cSourceDP[(point*2*NumSeg)+offset]   * inverse_n;
                pDatad[(index*2)+1] = cSourceDP[(point*2*NumSeg)+offset+1] * inverse_n;       
                pDatad[point*2]     = cSourceDP[(index*2*NumSeg)+offset]   * inverse_n;
                pDatad[(point*2)+1] = cSourceDP[(index*2*NumSeg)+offset+1] * inverse_n;
            }
        }
        // transform each set
        d = 1;
        for(index=1;index<=Tsize;index++)
        {
            d2 = d + d;
            jinc = 2;
            if (d > 2) jinc = 4;
            for (j=0; j<d; j=j+jinc)
            {

                ExpReal1DP = dExpDP[(Nsize / d2) * j * 2];
                ExpImag1DP = dExpDP[(Nsize / d2) * j * 2+1];
                ExpReal2DP = dExpDP[(Nsize / d2) * (j+1)*2];
                ExpImag2DP = dExpDP[(Nsize / d2) * (j+1)*2+1];

                if (d > 2)
                { 
                    ExpReal3DP = dExpDP[(Nsize / d2) * (j+2)*2];
                    ExpImag3DP = dExpDP[(Nsize / d2) * (j+2)*2+1];    
                    ExpReal4DP = dExpDP[(Nsize / d2) * (j+3)*2];
                    ExpImag4DP = dExpDP[(Nsize / d2) * (j+3)*2+1];
                }

                if (d == 1)
                {
                    for(k=0;k<Nsize;k=k+d2)
                    {
                      jk      = (j + k) * 2;
                      jkd = (j + k + d) * 2;
                      tempReal   = (pDatad[jkd]   * ExpReal1DP) - (pDatad[jkd+1] * ExpImag1DP);
                      tempImag   = (pDatad[jkd+1] * ExpReal1DP) + (pDatad[jkd]   * ExpImag1DP);
                      pDatad[jkd]  = pDatad[jk] - tempReal;
                      pDatad[jkd+1]  = pDatad[jk+1] - tempImag;
                      pDatad[jk]   = pDatad[jk] + tempReal;
                      pDatad[jk+1]   = pDatad[jk+1] + tempImag;
                    }
                }
                else if (d == 2)
                {
                    for(k=0;k<Nsize;k=k+d2)
                    {
                      jk      = (j + k) * 2;
                      jkd = (j + k + d) * 2;
                      tempReal   = (pDatad[jkd]   * ExpReal1DP) - (pDatad[jkd+1] * ExpImag1DP);
                      tempImag   = (pDatad[jkd+1] * ExpReal1DP) + (pDatad[jkd]   * ExpImag1DP);
                      pDatad[jkd]  = pDatad[jk] - tempReal;
                      pDatad[jkd+1]  = pDatad[jk+1] - tempImag;
                      pDatad[jk]   = pDatad[jk] + tempReal;
                      pDatad[jk+1]   = pDatad[jk+1] + tempImag;
        
                      tempReal   = (pDatad[jkd+2] * ExpReal2DP) - (pDatad[jkd+3] * ExpImag2DP);
                      tempImag   = (pDatad[jkd+3] * ExpReal2DP) + (pDatad[jkd+2] * ExpImag2DP);
                      pDatad[jkd+2]  = pDatad[jk+2] - tempReal;
                      pDatad[jkd+3]  = pDatad[jk+3] - tempImag;
                      pDatad[jk+2]   = pDatad[jk+2] + tempReal;
                      pDatad[jk+3]   = pDatad[jk+3] + tempImag;
                    }
                }
                else
                {
                    for(k=0;k<Nsize;k=k+d2)
                    {
                      jk      = (j + k) * 2;
                      jkd = (j + k + d) * 2;
                      tempReal   = (pDatad[jkd]   * ExpReal1DP) - (pDatad[jkd+1] * ExpImag1DP);
                      tempImag   = (pDatad[jkd+1] * ExpReal1DP) + (pDatad[jkd]   * ExpImag1DP);
                      pDatad[jkd]  = pDatad[jk] - tempReal;
                      pDatad[jkd+1]  = pDatad[jk+1] - tempImag;
                      pDatad[jk]   = pDatad[jk] + tempReal;
                      pDatad[jk+1]   = pDatad[jk+1] + tempImag;
        
                      tempReal   = (pDatad[jkd+2] * ExpReal2DP) - (pDatad[jkd+3] * ExpImag2DP);
                      tempImag   = (pDatad[jkd+3] * ExpReal2DP) + (pDatad[jkd+2] * ExpImag2DP);
                      pDatad[jkd+2]  = pDatad[jk+2] - tempReal;
                      pDatad[jkd+3]  = pDatad[jk+3] - tempImag;
                      pDatad[jk+2]   = pDatad[jk+2] + tempReal;
                      pDatad[jk+3]   = pDatad[jk+3] + tempImag;
        
                      tempReal   = (pDatad[jkd+4] * ExpReal3DP) - (pDatad[jkd+5] * ExpImag3DP);
                      tempImag   = (pDatad[jkd+5] * ExpReal3DP) + (pDatad[jkd+4] * ExpImag3DP);
                      pDatad[jkd+4]  = pDatad[jk+4] - tempReal;
                      pDatad[jkd+5]  = pDatad[jk+5] - tempImag;
                      pDatad[jk+4]   = pDatad[jk+4] + tempReal;
                      pDatad[jk+5]   = pDatad[jk+5] + tempImag;
        
                      tempReal   = (pDatad[jkd+6] * ExpReal4DP) - (pDatad[jkd+7] * ExpImag4DP);
                      tempImag   = (pDatad[jkd+7] * ExpReal4DP) + (pDatad[jkd+6] * ExpImag4DP);
                      pDatad[jkd+6]  = pDatad[jk+6] - tempReal;
                      pDatad[jkd+7]  = pDatad[jk+7] - tempImag;
                      pDatad[jk+6]   = pDatad[jk+6] + tempReal;
                      pDatad[jk+7]   = pDatad[jk+7] + tempImag;
                    }
                }                            
            }
            d = d2;
         }
    }

    //***********************************************************
    // This will recombine the above work units (if necessary) 
    //  Only run when t > TsizeL2 eg TsizeL2 = 13 run at size 16384
    //**********************************************************

   for(index=TsizeL2+1; index<=t; index++)
   {
      d2 = d + d;
      for (j=0; j<d; j=j+4)
      {
        ExpReal1DP = cExpDP[(n / d2 * j)*2];
        ExpImag1DP = cExpDP[(n / d2 * j)*2+1];
        ExpReal2DP = cExpDP[(n / d2 * (j+1))*2];
        ExpImag2DP = cExpDP[(n / d2 * (j+1))*2+1];
        ExpReal3DP = cExpDP[(n / d2 * (j+2))*2];
        ExpImag3DP = cExpDP[(n / d2 * (j+2))*2+1];
        ExpReal4DP = cExpDP[(n / d2 * (j+3))*2];
        ExpImag4DP = cExpDP[(n / d2 * (j+3))*2+1];
        for(k=0;k<n;k=k+d2)
        {
          jk      = (j + k) * 2;
          jkd = (j + k + d) * 2;
          tempReal   = (cDataDP[jkd]   * ExpReal1DP) - (cDataDP[jkd+1] * ExpImag1DP);
          tempImag   = (cDataDP[jkd+1] * ExpReal1DP) + (cDataDP[jkd]   * ExpImag1DP);
          cDataDP[jkd]  = cDataDP[jk] - tempReal;
          cDataDP[jkd+1]  = cDataDP[jk+1] - tempImag;
          cDataDP[jk]   = cDataDP[jk] + tempReal;
          cDataDP[jk+1]   = cDataDP[jk+1] + tempImag;

          tempReal   = (cDataDP[jkd+2] * ExpReal2DP) - (cDataDP[jkd+3] * ExpImag2DP);
          tempImag   = (cDataDP[jkd+3] * ExpReal2DP) + (cDataDP[jkd+2] * ExpImag2DP);
          cDataDP[jkd+2]  = cDataDP[jk+2] - tempReal;
          cDataDP[jkd+3]  = cDataDP[jk+3] - tempImag;
          cDataDP[jk+2]   = cDataDP[jk+2] + tempReal;
          cDataDP[jk+3]   = cDataDP[jk+3] + tempImag;

          tempReal   = (cDataDP[jkd+4] * ExpReal3DP) - (cDataDP[jkd+5] * ExpImag3DP);
          tempImag   = (cDataDP[jkd+5] * ExpReal3DP) + (cDataDP[jkd+4] * ExpImag3DP);
          cDataDP[jkd+4]  = cDataDP[jk+4] - tempReal;
          cDataDP[jkd+5]  = cDataDP[jk+5] - tempImag;
          cDataDP[jk+4]   = cDataDP[jk+4] + tempReal;
          cDataDP[jk+5]   = cDataDP[jk+5] + tempImag;

          tempReal   = (cDataDP[jkd+6] * ExpReal4DP) - (cDataDP[jkd+7] * ExpImag4DP);
          tempImag   = (cDataDP[jkd+7] * ExpReal4DP) + (cDataDP[jkd+6] * ExpImag4DP);
          cDataDP[jkd+6]  = cDataDP[jk+6] - tempReal;
          cDataDP[jkd+7]  = cDataDP[jk+7] - tempImag;
          cDataDP[jk+6]   = cDataDP[jk+6] + tempReal;
          cDataDP[jk+7]   = cDataDP[jk+7] + tempImag;
        }
      }
      d = d2;
   }

} // fftDP

// create data to avoid errors

void createComplexDataSP (int n, float synthFreqI, float synthFreqQ)
{
  float  constI, constQ;
  float  rindex;
  int   index;
 
  constI = ((float)2.0  * PISP * synthFreqI) / (float)n;
  constQ = ((float)2.0  * PISP * synthFreqQ) / (float)n;

  for (index=0; index<n; index++)
  {
    rindex = (float)index;
    cSourceSP[index*2]     = (float)cos (constI * rindex);
    cSourceSP[(index*2)+1] = (float)sin (constQ * rindex);
  }
}

void createComplexDataDP (int n, double synthFreqI, double synthFreqQ)
{
  double  constI, constQ;
  double  rindex;
  int   index;
 
  constI = (2.0  * PIDP * synthFreqI) / (double)n;
  constQ = (2.0  * PIDP * synthFreqQ) / (double)n;

  for (index=0; index<n; index++)
  {
    rindex = (double)index;
    cSourceDP[index*2]     = cos (constI * rindex);
    cSourceDP[(index*2)+1] = sin (constQ * rindex);
  }
} // create data to avoid errors



// create exponent table

void createExpTableSP (int n, int sign)
{
    int   index, Nsize;
    float const0;
    float rindex;
    float fsign;
    float pi2;
    
    fsign = (float)sign;
    pi2 = (float)2.0 * PISP;  
    const0 = (pi2 * fsign)  / (float)n;
    for (index=0; index<n/2; index++)
    {
        rindex = (float)index;
        cExpSP[index*2]   = (float)cos (const0 * rindex);
        cExpSP[index*2+1] = (float)sin (const0 * rindex);
    }
    Nsize = (int)pow((double)2,(double)TsizeL2);
    if(n < Nsize) Nsize = n;
    const0 = (pi2 * fsign)  / (float)Nsize;
    for (index=0; index<Nsize/2; index++)
    {
        rindex = (float)index;
        dExpSP[index*2]      = (float)cos (const0 * rindex);
        dExpSP[(index*2)+1]  = (float)sin (const0 * rindex);
    }
}


void createExpTableDP (int n, int sign)
{
    int   index, Nsize;
    double const0;
    double rindex;
    double fsign;
    double pi2;
    
    fsign = (double)sign;
    pi2 = 2.0 * PIDP;  
    const0 = (pi2 * fsign)  / (double)n;
    for (index=0; index<n/2; index++)
    {
        rindex = (double)index;
        cExpDP[index*2]   = cos (const0 * rindex);
        cExpDP[index*2+1] = sin (const0 * rindex);
    }
    Nsize = (int)pow((double)2,(double)TsizeL2);
    if(n < Nsize) Nsize = n;
    const0 = (pi2 * fsign)  / (double)Nsize;
    for (index=0; index<Nsize/2; index++)
    {
        rindex = (double)index;
        dExpDP[index*2]     = cos (const0 * rindex);
        dExpDP[(index*2)+1] = sin (const0 * rindex);
    }
} // create exponent table

// bit reverse table

void bitrev (int t)
{
  int index;
  int point;
  int m;
  int p;
  int Nsize, Tsize;

  if(t > TsizeL2)
         Tsize = TsizeL2;
  else
          Tsize = t;

  Nsize = (int)pow((double)2,(double)Tsize);

  for (index=0; index<Nsize; index++)
  {
    p = 0;
    m = index;
    for (point=0; point<Tsize; point++)
    {
      p = p + p;
      p = p + (m & 1);
      m = m >> 1;
    }
    table[index] = p;
  }

} // bit reverse table


void checkNoiseSP()
{
    int k;
    float sqOne;
    float MaxNoise = 0.0;
    float AveNoise = 0.0;
    
    for(k=1;k<nSP;k++)
    {
        if(k != nSP/16)
        {
            sqOne = cDataSP[k*2] * cDataSP[k*2] + cDataSP[(k*2)+1] * cDataSP[(k*2)+1];
            if(sqOne > MaxNoise)
                    MaxNoise = sqOne;
            AveNoise = AveNoise + sqOne;
        }
    }
    AveNoise = AveNoise / (nSP-2);
    sqOne = cDataSP[(nSP/16)*2] * cDataSP[(nSP/16)*2] + cDataSP[((nSP/16)*2)+1] * cDataSP[((nSP/16)*2)+1];
    sprintf(checkSP, " SP %14e  %10e  %10e", sqOne, MaxNoise, AveNoise);
}


void checkNoiseDP()
{
    int k;
    double sqOne;
    double MaxNoise = 0.0;
    double AveNoise = 0.0;
    
    for(k=1;k<nDP;k++)
    {
        if(k != nDP/16)
        {
            sqOne = cDataDP[k*2] * cDataDP[k*2] + cDataDP[(k*2)+1] * cDataDP[(k*2)+1];
            if(sqOne > MaxNoise) MaxNoise = sqOne;
            AveNoise = AveNoise + sqOne;
        }
    }
    AveNoise = AveNoise / (nDP-2);
    sqOne = cDataDP[(nDP/16)*2] * cDataDP[(nDP/16)*2] + cDataDP[((nDP/16)*2)+1] * cDataDP[((nDP/16)*2)+1]; 
    sprintf(checkDP, " DP %14e  %10e  %10e", sqOne, MaxNoise, AveNoise);
}

   

