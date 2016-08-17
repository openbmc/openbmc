#include <stdio.h>
#include <math.h>
#include <stdlib.h>

double convert(long long l)
{
  return (double)l;
}

int main(int argc, char * argv[]) {

  long long l = 10;
  double f;
  double check = 10.0;

  f = convert(l);
  printf("convert: %lld => %f\n", l, f);
  if ( f != check ) exit(1);

  f = 1234.67;
  check = 1234.0;
  printf("floorf(%f) = %f\n", f, floorf(f));
  if ( floorf(f) != check) exit(1);

  return 0;
}
