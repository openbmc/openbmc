/*
 gcc-6 fft1.c cpuidc.c -lm -lrt -O3 -march=armv8-a  -o fft1-RPi64 
 version armv8 64 Bit FFT Benchmark Version 1.0
*/


 #include <stdio.h>       
 #include <stdlib.h>
 #include <math.h>
 #include <time.h>
 #include "cpuidh.h"

// ************* GLOBAL VARIABLES **********

 double   cDataDP[1048576][2];
 double   cExpDP[524288][2];
 float    cDataSP[1048576][2];
 float    cExpSP[524288][2];
 int      table[1048576];

 #define I       0
 #define Q       1
 

 float  PISP = (float)3.14159265;
 double PIDP = 3.14159265358979323846;

 int  nSP;
 int  nDP;

 char    checkSP[80];
 char    checkDP[80];
 char    resultchars[1200];
 
//  ************** PROTOPTYPES *****************

 void bitrev(int, int);
 void createExpTableSP(int n, int sign);
 void createExpTableDP(int n, int sign);
 void createRealDataSP(int n, float synthFreq);
 void createRealDataDP(int n, double synthFreq);
 void fftSP(int t, int n); 
 void fftDP(int t, int n);
 void checkNoiseSP();
 void checkNoiseDP();
 void runIt();


void main()
{
    int i, g;
    char version[80] = " armv8 64 Bit FFT Benchmark Version 1.0 ";
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
    printf("  Raspberry Pi 3 Running time > 30 seconds before results display\n\n"); 
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

  for (t=10; t<21; t++)
  {
    k = t-10;
    n = (int)pow(2,(double)t);
    nSP = n; 
    nDP = n;
    size[k] = n/1024;  
    bitrev (t, n); 
    sign = -1; 
    createExpTableSP(n, sign);
    synthFreq = (float)n/16;
    
    for(j=0;j<3;j++)
    {
        createRealDataSP(n, synthFreq);
        start_time(); 
        fftSP (t, n); 
        end_time();
        mt[k][j] = secs * 1000;
        checkNoiseSP();       
    }
    createExpTableDP(n, sign);
    for(j=0;j<3;j++)
    {
        createRealDataDP(n, synthFreq);
        start_time(); 
        fftDP (t, n); 
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

void fftSP (int t, int n)
{
  int   point;
  float  inverse_n;
  float  temp1;
  float  temp2;
  int   d;
  int   d2;
  int   index;
  int   j;
  int   k;
  int   jk;
  int   jkd;
  int   jnd2;
  register float  tempReal;
  register float  tempImag;

  inverse_n = 1.0f / (float)n;
  for (index=0; index<n; index++)
  {
    point = table[index];
    if (point >= index)
    {
      temp1 = cDataSP[index][I] * inverse_n;
      temp2 = cDataSP[index][Q] * inverse_n;
      cDataSP[index][I]   = cDataSP[point][I] * inverse_n;
      cDataSP[index][Q]   = cDataSP[point][Q] * inverse_n;
      cDataSP[point][I]   =       temp1;
      cDataSP[point][Q]   =       temp2;
   }
  }
    d = 1;
    index = 1;
    while (index <= t)
    {
      d2 = d + d;
      for (j=0; j<d; j++)
      {
        k = 0;
        jnd2 =  n / d2 * j; 
        while (k < n)
        {
          jk = j + k;
          jkd = jk + d;
          tempReal   = (cDataSP[jkd][I] * cExpSP[jnd2][I]) - (cDataSP[jkd][Q] * cExpSP[jnd2][Q]);
          tempImag   = (cDataSP[jkd][Q] * cExpSP[jnd2][I]) + (cDataSP[jkd][I] * cExpSP[jnd2][Q]);
          cDataSP[jkd][I]  = cDataSP[jk][I] - tempReal;
          cDataSP[jkd][Q]  = cDataSP[jk][Q] - tempImag;
          cDataSP[jk][I]   = cDataSP[jk][I] + tempReal;
          cDataSP[jk][Q]   = cDataSP[jk][Q] + tempImag;
          k = k + d2;
        }
      }
      index = index + 1;
      d = d2;
    }
}
void fftDP (int t, int n)
{
  int   point;
  double  inverse_n;
  double  temp1;
  double  temp2;
  int   d;
  int   d2;
  int   index;
  int   j;
  int   k;
  int   jk;
  int   jkd;
  int   jnd2;
  register double  tempReal;
  register double  tempImag;

  inverse_n = 1.0 / n;
  for (index=0; index<n; index++)
    {
    point = table[index];
    if (point >= index)
                  {
      temp1 = cDataDP[index][I] * inverse_n;
      temp2 = cDataDP[index][Q] * inverse_n;
      cDataDP[index][I]   = cDataDP[point][I] * inverse_n;
      cDataDP[index][Q]   = cDataDP[point][Q] * inverse_n;
      cDataDP[point][I]   =       temp1;
      cDataDP[point][Q]   =       temp2;
      }
    }
    d = 1;
    index = 1;
    while (index <= t)
    {
      d2 = d + d;
      for (j=0; j<d; j++)
      {
        k = 0;
        jnd2 =  n / d2 * j; 
        while (k < n)
        {
          jk = j + k;
          jkd = jk + d;
          tempReal   = (cDataDP[jkd][I] * cExpDP[jnd2][I]) - (cDataDP[jkd][Q] * cExpDP[jnd2][Q]);
          tempImag   = (cDataDP[jkd][Q] * cExpDP[jnd2][I]) + (cDataDP[jkd][I] * cExpDP[jnd2][Q]);
          cDataDP[jkd][I]  = cDataDP[jk][I] - tempReal;
          cDataDP[jkd][Q]  = cDataDP[jk][Q] - tempImag;
          cDataDP[jk][I]   = cDataDP[jk][I] + tempReal;
          cDataDP[jk][Q]   = cDataDP[jk][Q] + tempImag;
          k = k + d2;
        }
      }
      index = index + 1;
      d = d2;
    }
} // fftDP

// create data to avoid errors

void createRealDataSP (int n, float synthFreq)
{
    float  const0;
    float  rindex;
    int   index;
    
    const0 = (2.0f  * PISP * synthFreq) / (float)n;
    for (index=0; index<n; index++)
    {
        rindex = (float)index;
        cDataSP[index][I] = (float)cos (const0 * rindex);
        cDataSP[index][Q] = (float)sin (const0 * rindex);
    }
}
void createRealDataDP (int n, double synthFreq)
{
    double  const0;
    double  rindex;
    int   index;
    
    const0 = (2.0  * PIDP * synthFreq) / (double)n;
    for (index=0; index<n; index++)
    {
        rindex = (double)index;
        cDataDP[index][I] = cos (const0 * rindex);
        cDataDP[index][Q] = sin (const0 * rindex);
    }
} // create data to avoid errors


// create exponent table

void createExpTableSP (int n, int sign)
{
    int   index;
    float const0;
    float rindex;
    float fsign;
    float pi2;
    
    fsign = (float)sign;
    pi2 = 2.0f * PISP;  
    const0 = (pi2 * fsign)  / (float)n;
    for (index=0; index<n/2; index++)
    {
        rindex = (float)index;
        cExpSP[index][I] = (float)cos (const0 * rindex);
        cExpSP[index][Q] = (float)sin (const0 * rindex);
    }
}
void createExpTableDP (int n, int sign)
{
    int   index;
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
        cExpDP[index][I] = cos (const0 * rindex);
        cExpDP[index][Q] = sin (const0 * rindex);
    }
} // create exponent table


// bit reverse table

void bitrev (int t, int n)
{
    int index;
    int point;
    int m;
    int p;
    for (index=0; index<n; index++)
    {
        p = 0;
        m = index;
        for (point=0; point<t; point++)
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
            sqOne = cDataSP[k][I] * cDataSP[k][I] + cDataSP[k][Q] * cDataSP[k][Q];
            if(sqOne > MaxNoise)
                    MaxNoise = sqOne;
            AveNoise = AveNoise + sqOne;
        }
    }
    AveNoise = AveNoise / (nSP-2);
    sqOne = cDataSP[nSP/16][I] * cDataSP[nSP/16][I] + cDataSP[nSP/16][Q] * cDataSP[nSP/16][Q];
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
            sqOne = cDataDP[k][I] * cDataDP[k][I] + cDataDP[k][Q] * cDataDP[k][Q];
            if(sqOne > MaxNoise) MaxNoise = sqOne;
            AveNoise = AveNoise + sqOne;
        }
    }
    AveNoise = AveNoise / (nDP-2);
    sqOne = cDataDP[nDP/16][I] * cDataDP[nDP/16][I] + cDataDP[nDP/16][Q] * cDataDP[nDP/16][Q];
    sprintf(checkDP, " DP %14e  %10e  %10e", sqOne, MaxNoise, AveNoise);
}


