/*
* Copyright (c) 2018-2020 Ampere Computing LLC
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* This program is for updating FRU EEPROM device
*/

#include <fcntl.h>
#include <linux/errno.h>
#include <linux/i2c-dev.h>
#include <linux/i2c.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <time.h>
#include <unistd.h>
#include <zlib.h>

#pragma pack(1)

static char fru_device[128] = "";
static char fru_image[128] = "";

static void display_usage(void) {
	printf("Usage: ampere_fru_upgrade <args>\n");
	printf("Arguments:\n");
	printf("\t-d <dev> \t: FRU sysfs device\n");
	printf("\t-f <file>\t: The FRU file\n");
}

static int image_arg_handler(int argc, char **argv, int index) {
	memset(&fru_image, '\0', sizeof(fru_image));
	strcpy((char *)&fru_image, argv[index + 1]);
	return 0;
}

static int device_arg_handler(int argc, char **argv, int index) {
	memset(&fru_device, '\0', sizeof(fru_device));
	strcpy((char *)&fru_device, argv[index + 1]);
	return 0;
}

static char *arglist[] = {"-d", "-f", NULL};

static int (*handlerlist[])(int, char **, int) = {
	device_arg_handler,
	image_arg_handler,
	NULL
};

static int parse_arguments(int argc, char **argv) {
	int i, j, retval;

	if (argc <= 1)
		return -EINVAL;
	for (i = 1; i < argc; i++) {
		j = 0;
arg_list_loop:
		if (arglist[j] == NULL)
			continue;
		if (strcmp(argv[i], arglist[j]) == 0) {
			retval = handlerlist[j](argc, argv, i);
			if (retval >= 0) {
				i += retval;
			} else {
				fprintf(stderr, "Invalid argument: %s\n", arglist[j]);
				return -EINVAL;
			}
		}
		j++;
		goto arg_list_loop;
	}
	return 0;
}

static int verify_valid_image(uint32_t crc32_checksum, ssize_t sz) {
	FILE *fru_device_file;
	char *buf;
	uint32_t checksum;
	int ret = 0;

	/* Read FW file to buffer */
	fru_device_file = fopen(fru_device, "rb");
	if (!fru_device_file) {
		printf("Can't open file for reading\n");
		return -EINVAL;
	}
	fseek(fru_device_file, 0, SEEK_END);
	buf = malloc(sz);
	if (!buf) {
		printf("Not enough memory\n");
		fclose(fru_device_file);
		return -ENOMEM;
	}
	fseek(fru_device_file, 0, SEEK_SET);
	fread(buf, sz, 1, fru_device_file);

	/* Get checksum of input data */
	checksum = crc32(0, (unsigned char *)buf, sz);

	if (checksum != crc32_checksum) {
		printf("Mismatch data!");
		ret = -EIO;
	}

	fclose(fru_device_file);

	return ret;
}

int main(int argc, char **argv) {
	FILE *fru_image_file;
	FILE *fru_device_file;
	int ret;
	ssize_t sz;
	char *buf;
	uint32_t crc32_checksum;

	/* Parsing the arguments */
	ret = parse_arguments(argc, argv);
	if (ret < 0) {
		printf("syntax error!\n");
		display_usage();
		return -EOPNOTSUPP;
	}

	/* Read FW file to buffer */
	fru_image_file = fopen(fru_image, "rb");
	if (!fru_image_file) {
		printf("Can't open file for reading\n");
		return -EINVAL;
	}
	fseek(fru_image_file, 0, SEEK_END);
	sz = ftell(fru_image_file);
	buf = malloc(sz);
	if (!buf) {
		printf("Not enough memory\n");
		fclose(fru_image_file);
		return -ENOMEM;
	}
	fseek(fru_image_file, 0, SEEK_SET);
	fread(buf, sz, 1, fru_image_file);

	/* Get checksum of input data */
	crc32_checksum = crc32(0, (unsigned char *)buf, sz);

	/* Write into FRU device */
	fru_device_file = fopen(fru_device, "rb+");
	if (!fru_device_file) {
		printf("Can't open device for upgrading\n");
		fclose(fru_image_file);
		return -EINVAL;
	}
	if (fseek(fru_device_file, 0, SEEK_END)) {
		/* Error when accessing file */
		/* Close files and skip checksum */
		crc32_checksum = 0;
		ret = -EIO;
		goto closefiles;
	}
	rewind(fru_device_file);
	if (fwrite(buf, sz, 1, fru_device_file) != 1) {
		/* Error when accessing file */
		/* Close files and skip checksum */
		crc32_checksum = 0;
		ret = -EIO;
		goto closefiles;
	}

	fflush(fru_device_file);
	fsync(fileno(fru_device_file));

closefiles:
	free(buf);
	fclose(fru_image_file);
	fclose(fru_device_file);

	/* Verify the image by checking the checksum */
	if (crc32_checksum != 0) {
		ret = verify_valid_image(crc32_checksum, sz);
	}

	return ret;
}
