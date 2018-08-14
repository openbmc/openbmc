#include <mhash.h>
#include <stdio.h>

int main()
{

       char password[] = "Jefe";
       int keylen = 4;
       char data[] = "what do ya want for nothing?";
       int datalen = 28;
       MHASH td;
       unsigned char mac[16];
       int j;

       td = mhash_hmac_init(MHASH_MD5, password, keylen, mhash_get_hash_pblock(MHASH_MD5));

       mhash(td, data, datalen);
       mhash_hmac_deinit(td, mac);

/*
 * The output should be 0x750c783e6ab0b503eaa86e310a5db738
 * according to RFC 2104.
 */

       printf("0x");
       for (j = 0; j < mhash_get_block_size(MHASH_MD5); j++) {
               printf("%.2x", mac[j]);
       }
       printf("\n");

       exit(0);
}
