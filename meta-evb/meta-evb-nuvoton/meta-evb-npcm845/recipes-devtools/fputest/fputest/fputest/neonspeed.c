/*
  gcc-6 neonspeed.c cpuidc.c -lm -lrt -O3 -march=armv8-a -o NeonSpeedPi64
*/


 #include <stdio.h>
 #include <stdlib.h>
 #include <string.h>
 #include <math.h>
 #include <time.h> 
 #include <arm_neon.h>
 #include "cpuidh.h"

 #define version  "armv8 64 Bit V 1.0"

 int n1;

 float   xs[8388608];  // 32 MB
 float   ys[8388608];  // 32 MB

 int    * xi; 
 int    * yi;

 int errors;
 double runSecs = 0.2;
 //double startSecs;
 //double  secs;
 char resultchars[1000];
 char msg[100] = "";
 

  void checkTime()
  {
     if (secs < runSecs)
     {
          if (secs < runSecs / 8.0)
          {
                n1 = n1 * 10;
          }
          else
          {
                n1 = (int)(runSecs * 1.25 / secs * (double)n1+1);
          }
      }
  }

void floatvplusvxc(float32_t *x, float32_t *y, float32_t *c, int size)
{
  int i;
  float32x4_t x41, y41, z41;
  float32x4_t x42, y42, z42;
  float32x4_t x43, y43, z43;
  float32x4_t x44, y44, z44;
  float32x4_t c4;

  float32_t *ptrx1 = x;
  float32_t *ptry1 = y;
  float32_t *ptrx2 = x;
  float32_t *ptry2 = y;
  float32_t *ptrx3 = x;
  float32_t *ptry3 = y;
  float32_t *ptrx4 = x;
  float32_t *ptry4 = y;
  ptrx2 = ptrx2 + 4;
  ptry2 = ptry2 + 4;
  ptrx3 = ptrx3 + 8;
  ptry3 = ptry3 + 8;
  ptrx4 = ptrx4 + 12;
  ptry4 = ptry4 + 12;

  float32_t *ptrc = c;
  c4 = vld1q_f32(ptrc);

  for(i=0; i < size/16; i++)
  {
    x41 = vld1q_f32(ptrx1);
    x42 = vld1q_f32(ptrx2);
    x43 = vld1q_f32(ptrx3);
    x44 = vld1q_f32(ptrx4);

    y41 = vld1q_f32(ptry1);
    y42 = vld1q_f32(ptry2);
    y43 = vld1q_f32(ptry3);
    y44 = vld1q_f32(ptry4);
   
    z41 = vmlaq_f32(x41, y41, c4);
    z42 = vmlaq_f32(x42, y42, c4);
    z43 = vmlaq_f32(x43, y43, c4);
    z44 = vmlaq_f32(x44, y44, c4);

    vst1q_f32(ptrx1, z41);
    vst1q_f32(ptrx2, z42);
    vst1q_f32(ptrx3, z43);
    vst1q_f32(ptrx4, z44);

    ptrx1 = ptrx1 + 16;
    ptry1 = ptry1 + 16;
    ptrx2 = ptrx2 + 16;
    ptry2 = ptry2 + 16;
    ptrx3 = ptrx3 + 16;
    ptry3 = ptry3 + 16;
    ptrx4 = ptrx4 + 16;
    ptry4 = ptry4 + 16;
  }
}

void floatvplusv(float32_t *x, float32_t *y, int size)
{
  int i;
  float32x4_t x41, y41, z41;
  float32x4_t x42, y42, z42;
  float32x4_t x43, y43, z43;
  float32x4_t x44, y44, z44;

  float32_t *ptrx1 = x;
  float32_t *ptry1 = y;
  float32_t *ptrx2 = x;
  float32_t *ptry2 = y;
  float32_t *ptrx3 = x;
  float32_t *ptry3 = y;
  float32_t *ptrx4 = x;
  float32_t *ptry4 = y;
  ptrx2 = ptrx2 + 4;
  ptry2 = ptry2 + 4;
  ptrx3 = ptrx3 + 8;
  ptry3 = ptry3 + 8;
  ptrx4 = ptrx4 + 12;
  ptry4 = ptry4 + 12;

  for(i=0; i < size/16; i++)
  {
    x41 = vld1q_f32(ptrx1);
    x42 = vld1q_f32(ptrx2);
    x43 = vld1q_f32(ptrx3);
    x44 = vld1q_f32(ptrx4);

    y41 = vld1q_f32(ptry1);
    y42 = vld1q_f32(ptry2);
    y43 = vld1q_f32(ptry3);
    y44 = vld1q_f32(ptry4);
   
    z41 = vaddq_f32(x41, y41);;
    z42 = vaddq_f32(x42, y42);
    z43 = vaddq_f32(x43, y43);
    z44 = vaddq_f32(x44, y44);

    vst1q_f32(ptrx1, z41);
    vst1q_f32(ptrx2, z42);
    vst1q_f32(ptrx3, z43);
    vst1q_f32(ptrx4, z44);

    ptrx1 = ptrx1 + 16;
    ptry1 = ptry1 + 16;
    ptrx2 = ptrx2 + 16;
    ptry2 = ptry2 + 16;
    ptrx3 = ptrx3 + 16;
    ptry3 = ptry3 + 16;
    ptrx4 = ptrx4 + 16;
    ptry4 = ptry4 + 16;
  }
}

void intvplusvplusc(uint32_t *x, uint32_t *y, uint32_t *c, int size)
{
  int i;
  uint32x4_t x41, y41, z41;
  uint32x4_t x42, y42, z42;
  uint32x4_t x43, y43, z43;
  uint32x4_t x44, y44, z44;
  uint32x4_t c4;

  uint32_t *ptrx1 = x;
  uint32_t *ptry1 = y;
  uint32_t *ptrx2 = x;
  uint32_t *ptry2 = y;
  uint32_t *ptrx3 = x;
  uint32_t *ptry3 = y;
  uint32_t *ptrx4 = x;
  uint32_t *ptry4 = y;
  ptrx2 = ptrx2 + 4;
  ptry2 = ptry2 + 4;
  ptrx3 = ptrx3 + 8;
  ptry3 = ptry3 + 8;
  ptrx4 = ptrx4 + 12;
  ptry4 = ptry4 + 12;

  uint32_t *ptrc = c;
  c4 = vld1q_u32(ptrc);

  for(i=0; i < size/16; i++)
  {
    x41 = vld1q_u32(ptrx1);
    x42 = vld1q_u32(ptrx2);
    x43 = vld1q_u32(ptrx3);
    x44 = vld1q_u32(ptrx4);

    y41 = vld1q_u32(ptry1);
    y42 = vld1q_u32(ptry2);
    y43 = vld1q_u32(ptry3);
    y44 = vld1q_u32(ptry4);

    z41 = vaddq_u32(x41, y41);
    z42 = vaddq_u32(x42, y42);
    z43 = vaddq_u32(x43, y43);
    z44 = vaddq_u32(x44, y44);

    z41 = vaddq_u32(z41, c4);
    z42 = vaddq_u32(z42, c4);
    z43 = vaddq_u32(z43, c4);
    z44 = vaddq_u32(z44, c4);

    vst1q_u32(ptrx1, z41);
    vst1q_u32(ptrx2, z42);
    vst1q_u32(ptrx3, z43);
    vst1q_u32(ptrx4, z44);

    ptrx1 = ptrx1 + 16;
    ptry1 = ptry1 + 16;
    ptrx2 = ptrx2 + 16;
    ptry2 = ptry2 + 16;
    ptrx3 = ptrx3 + 16;
    ptry3 = ptry3 + 16;
    ptrx4 = ptrx4 + 16;
    ptry4 = ptry4 + 16;
  }
}

void intvplusv(uint32_t *x, uint32_t *y, int size)
{
  int i;
  uint32x4_t x41, y41, z41;
  uint32x4_t x42, y42, z42;
  uint32x4_t x43, y43, z43;
  uint32x4_t x44, y44, z44;

  uint32_t *ptrx1 = x;
  uint32_t *ptry1 = y;
  uint32_t *ptrx2 = x;
  uint32_t *ptry2 = y;
  uint32_t *ptrx3 = x;
  uint32_t *ptry3 = y;
  uint32_t *ptrx4 = x;
  uint32_t *ptry4 = y;
  ptrx2 = ptrx2 + 4;
  ptry2 = ptry2 + 4;
  ptrx3 = ptrx3 + 8;
  ptry3 = ptry3 + 8;
  ptrx4 = ptrx4 + 12;
  ptry4 = ptry4 + 12;

  for(i=0; i < size/16; i++)
  {
    x41 = vld1q_u32(ptrx1);
    x42 = vld1q_u32(ptrx2);
    x43 = vld1q_u32(ptrx3);
    x44 = vld1q_u32(ptrx4);

    y41 = vld1q_u32(ptry1);
    y42 = vld1q_u32(ptry2);
    y43 = vld1q_u32(ptry3);
    y44 = vld1q_u32(ptry4);

    z41 = vaddq_u32(x41, y41);
    z42 = vaddq_u32(x42, y42);
    z43 = vaddq_u32(x43, y43);
    z44 = vaddq_u32(x44, y44);

    vst1q_u32(ptrx1, z41);
    vst1q_u32(ptrx2, z42);
    vst1q_u32(ptrx3, z43);
    vst1q_u32(ptrx4, z44);

    ptrx1 = ptrx1 + 16;
    ptry1 = ptry1 + 16;
    ptrx2 = ptrx2 + 16;
    ptry2 = ptry2 + 16;
    ptrx3 = ptrx3 + 16;
    ptry3 = ptry3 + 16;
    ptrx4 = ptrx4 + 16;
    ptry4 = ptry4 + 16;
  }
}

int mainLoops(int size)
{

    if (size == 0) errors = 0;
       
    int passes[25];

    int useMem;   

    
    float  sums;
    float  cf[4];
    
    float  ones = 1.0;
    float  zeros = 0;
      
    int g, i, m, nn;
    int kd, ks, ki, mem;

    int  sumi;
    int  ci1[4];
    int  ci2[4];

    int  zeroi = 0;
    int onei = 1;
             

    double mbpsf1, mbpsn1, mbpsi1, mbpsin;
    double mbpss2, mbpsi2;
    double memMB;
    int    res1, res2, res3, res4, res5, res6;
    
    passes[0]  =      1000;   //     16 KB
    passes[1]  =      2000;   //     32 KB
    passes[2]  =      4000;   //     64 KB
    passes[3]  =      8000;   //    128 KB
    passes[4]  =     16000;   //    256 KB
    passes[5]  =     32000;   //    512 kB
    passes[6]  =     64000;   //      1 MB
    passes[7]  =    256000;   //      4 MB
    passes[8]  =   1024000;   //     16 MB
    passes[9]  =   4096000;   //     64 MB  - Max here runs = 10
    passes[10] =  16384000;   //    256 MB
    passes[11] =  65536000;   //   1024 MB
 
    int   *xi = (int *) xs;
    int   *yi = (int *) ys;     
    float32_t *xsv = (float32_t *) xs;
    float32_t *ysv = (float32_t *) ys;

    float32_t *cfv = (float32_t *) cf;
      
    kd = passes[size];
    
    nn = 6400000 / kd;
    if (nn < 1) nn = 1;
    
    ks = kd * 2;

    ki = kd * 2; 
    
    memMB = (double)kd * 16.0 / 1000000;
    mem = (int)((double)kd * 16.0 / 1000);
      
    n1 = nn;
    do
    {
        sums = 1.0001;
        for (m=0; m<ks; m++)
        {
              xs[m] = ones;
              ys[m] = ones;
        }          
        start_time();
        for (i=0; i<n1; i++)
        {
            for (m=0; m<ks; m=m+4)
            {
               xs[m]   = xs[m]   + sums * ys[m];
               xs[m+1] = xs[m+1] + sums * ys[m+1];
               xs[m+2] = xs[m+2] + sums * ys[m+2];
               xs[m+3] = xs[m+3] + sums * ys[m+3];
            }
        }
        end_time();
        checkTime();
    }
    while (secs < runSecs);
    
    mbpsf1 = (double)n1 * memMB / secs;
    res1 = (int)xs[0];
    if (xs[0] != xs[ks-1])
    {
        res1 = 9999;
        errors = errors + 1;
    }
    n1 = nn;
    for (i=0; i<4; i++)
    {
        cf[i] = 1.0001;;
    }
    do
    {
        for (m=0; m<ks; m++)
        {
              xs[m] = ones;
              ys[m] = ones;
        }          
        start_time();
        for (i=0; i<n1; i++)
        {
            floatvplusvxc(xsv, ysv, cfv, ks);
        }
        end_time();
        checkTime();
    }
    while (secs < runSecs);

    mbpsn1 =  (double)n1 * memMB / secs;
    res2 = (int)xs[0];;
    if (xs[0] != xs[ks-1])
    {
        res2 = 9999;
        errors = errors + 1;
    }
     
    n1 = nn;
    do
    {
        sumi = nn;
        for (m=0; m<ki; m++)
        {
              xi[m] = zeroi;
              yi[m] = onei;
        }          
                
        start_time();

        for (i=0; i<n1; i++)
        {
           for (m=0; m<ki; m=m+4)
           {
               xi[m]   = xi[m]   + sumi + yi[m];
               xi[m+1] = xi[m+1] + sumi + yi[m+1];
               xi[m+2] = xi[m+2] + sumi + yi[m+2];
               xi[m+3] = xi[m+3] + sumi + yi[m+3];
            }
            sumi = -sumi;
        }

        end_time();
        checkTime();
    }
    while (secs < runSecs);
              
    mbpsi1 = (double)n1 * memMB / secs;
    res3 = xi[0];
    if (xi[0] != xi[ki-1])
    {
        res3 = 9999;
        errors = errors + 1;
    }
        
    n1 = nn;
    for (i=0; i<4; i++)
    {
        ci1[i] = nn;
        ci2[i] = -nn;
    }
    do
    {
        for (m=0; m<ki; m++)
        {
              xi[m] = zeroi;
              yi[m] = onei;
        }          
                
        start_time();

        for (i=0; i<n1; i=i+2)
        {
           intvplusvplusc(xi, yi, ci1, ki);
           intvplusvplusc(xi, yi, ci2, ki);
        }

        end_time();
        checkTime();
    }
    while (secs < runSecs);

    mbpsin = (double)n1 * memMB / secs;
    res4 = xi[0];
    if (xi[0] != xi[ki-1])
    {
        res4 = 9999;
        errors = errors + 1;
    }

    n1 = nn;
    do
    {
        for (m=0; m<ks; m++)
        {
              xs[m] = zeros;
              ys[m] = ones;
        }          
        start_time();
        for (i=0; i<n1; i++)
        {
            floatvplusv(xsv, ysv, ks);
        }
        end_time();
        checkTime();
    }
    while (secs < runSecs);
        

    sums = xs[1];
    mbpss2 = (double)n1 * memMB / secs;
    res5 = (int)xs[0];
    if (xs[0] != xs[ki-1])
    {
        res5 = 9999;
        errors = errors + 1;
    }

    n1 = nn;
    do
    {
        for (m=0; m<ki; m++)
        {
              xi[m] = zeroi;
              yi[m] = onei;
        }          
                
        start_time();
        for (i=0; i<n1; i++)
        {
           intvplusv(xi, yi, ki);
        }
        end_time();
        checkTime();
    }
    while (secs < runSecs);
 
    sumi = xi[1];
    mbpsi2 = (double)n1 * memMB / secs;
    res6 =  xi[0];
    if (xi[0] != xi[ki-1])
    {
        res6 = 9999;
        errors = errors + 1;
    }
    
   if (errors > 0 && size == 9) sprintf(msg, "\n %d errors detected", errors);
   sprintf(resultchars, "%8d%7d%7d%7d%7d%7d%7d%s\n",
                          mem, (int)mbpsf1, (int)mbpsn1, (int)mbpsi1,
                               (int)mbpsin, (int)mbpss2, (int)mbpsi2, msg);

   return 0;
}

int main()
{
    int i, g;
    FILE    *outfile;
    
    outfile = fopen("neonSpeed.txt","a+");
    if (outfile == NULL)
    {
        printf ("Cannot open results file \n\n");
        printf(" Press Enter\n");
        g  = getchar();
        exit (0);
    }
    printf("\n");
    getDetails();
 
    printf(" #############################################\n"); 
    fprintf (outfile, " ##############################################\n");                     

    local_time();
    
    printf("\n  NEON Speed Test %s %s\n", version, timeday);
    printf("       Vector Reading Speed in MBytes/Second\n");
    printf("  Memory  Float v=v+s*v  Int v=v+v+s   Neon v=v+v\n");
    printf("  KBytes   Norm   Neon   Norm   Neon  Float    Int\n\n");
    fprintf(outfile, "\n  NEON Speed Test %s %s\n", version, timeday);
    fprintf(outfile, "       Vector Reading Speed in MBytes/Second\n");
    fprintf(outfile, "  Memory  Float v=v+s*v  Int v=v+v+s   Neon v=v+v\n");
    fprintf(outfile, "  KBytes   Norm   Neon   Norm   Neon  Float    Int\n\n");


    for (i=0; i<10; i++)
    {
        mainLoops(i);
        printf("%s", resultchars);
        fprintf(outfile, "%s", resultchars);
    }

    local_time();
    printf("\n       End of test %s\n", timeday);
    fprintf(outfile, "\n       End of test %s", timeday);        

    fprintf (outfile, "\nSYSTEM INFORMATION\n\nFrom File /proc/cpuinfo\n");
    fprintf (outfile, "%s \n", configdata[0]);
    fprintf (outfile, "\nFrom File /proc/version\n");
    fprintf (outfile, "%s \n", configdata[1]);
    fprintf (outfile, "\n");
    fflush(outfile);

    char moredata[1024];
    printf("Type additional information to include in memSpeed.txt - Press Enter\n");
    if (fgets (moredata, sizeof(moredata), stdin) != NULL)
             fprintf (outfile, "Additional information - %s\n", moredata);

    fflush(outfile);
    fclose(outfile);
    return 1;
}



