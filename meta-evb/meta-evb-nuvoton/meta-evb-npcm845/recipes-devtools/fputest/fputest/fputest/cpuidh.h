
#ifdef __cplusplus
extern "C" {
#endif

extern char    configdata[2][1024];
extern char    timeday[30];
extern double  theseSecs;
extern double  startSecs;
extern double  secs;
extern  double ramGB;
extern  int    megaHz;
extern int     CPUconf;
extern int     CPUavail;

int getDetails();
void local_time();
void start_time();
void end_time();

#ifdef __cplusplus
};
#endif


