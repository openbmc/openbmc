/*
 * Copyright (c) 2020 Ampere Computing LLC
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
 * This is NVPARAM setting application which is used to:
 *  - Program individual NVPARAM entry
 *  - Program a full NVPARAM based on the output of nvgen command
 *  - Clear NVPARAM area
 */

#include <errno.h>
#include <fcntl.h>
#include <getopt.h>
#include <mtd/mtd-user.h>
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <limits.h>
#include <dirent.h>

typedef unsigned char uint8_t;
typedef unsigned int uint32_t;
#define PERCENTAGE(x, total)    (((x) * 100) / (total))
#define KB(x)                   ((x) / 1024)

/* size of read/write buffer */
#define BUFSIZE                 (10 * 1024)

/* error levels */
#define LOG_NORMAL              1
#define LOG_ERROR               2

#define PROC_MTD_INFO           "/proc/mtd"
#define HOST_SPI_FLASH_MTD_NAME "pnor"
#define MTD_DEV_SIZE            20

/* Option string of this application */
#define OPTION_STRING	"cd:ef:hlo:rs:"
enum {
	OPTION_C = 0,
	OPTION_D,
	OPTION_F,
	OPTION_H,
	OPTION_O,
	OPTION_R,
	OPTION_S,
	OPTION_E,
	OPTION_L,
	MAX_OPTIONS,
};

/*
 * According to Altra Interface Firmware Requirement,
 * size of NVPARAM partitions is 64KB.
 */
#define NVPARAM_PARTITION_SIZE	(64 *1024)
struct nvparam_entry {
	uint32_t param1;
	uint32_t acl_rd:8;
	uint32_t acl_wr:7;
	uint32_t valid:1;
	uint32_t crc16:16;
} __attribute__((__packed__));

struct mtd_info_user mtd;
struct stat filestat;
struct erase_info_user erase;

/*----------------------------------------------------------------------------
 * @fn log_printf
 *
 * @brief Print logs
 * @params  level [IN] - Console output selection
 * 			fmt [IN] - Text to print
 *--------------------------------------------------------------------------*/
static void log_printf(int level, const char *fmt, ...)
{
	FILE *fp = level == LOG_NORMAL ? stdout : stderr;
	va_list ap;

	va_start(ap, fmt);
	vfprintf(fp, fmt, ap);
	va_end(ap);
	fflush(fp);
}

/*----------------------------------------------------------------------------
 * @fn validate_input_file
 *
 * @brief Check if the file name user input is valid.
 * Criteria: File status is successfully retrieved, file size doesn't
 * exist host SPI NOR partition size.
 * @params  fil_fd [IN] - The file descriptor of user input file
 * 			filename [IN] - The string of user input file name
 * @return  0 - Valid input
 * 			1 - Invalid input
 *--------------------------------------------------------------------------*/
static int validate_input_file(int fil_fd, char *filename)
{
	int ret = 0;

	if (fstat(fil_fd, &filestat) < 0) {
		log_printf(LOG_ERROR, "While trying to get the file"
			"status of %s\n", filename);
		ret = -1;
	}

	/* Checking file size whether the device will accumulate or not? */
	if ((unsigned)filestat.st_size > mtd.size) {
		log_printf(LOG_ERROR, "%s won't fit into SPI NOR partition!\n",
			filename);
		ret = -1;
	}

	return ret;
}

/*----------------------------------------------------------------------------
 * @fn validate_input_offset
 *
 * @brief Check if the offset user input is valid.
 * Criteria: The offset must be in range of host SPI NOR partition, and align
 * to SPI NOR erase block size
 * @params  offset [IN] - The user input offset
 * @return  0 - Valid input
 * 			1 - Invalid input
 *--------------------------------------------------------------------------*/
static int validate_input_offset(ulong offset)
{
	int ret = 0;

	if (offset > mtd.size) {
		log_printf(LOG_ERROR, "offset:0x%x not with-in range "
			"of mtd partition size:0x%x\n", offset, mtd.size);
		ret = -1;
	}

	if ((offset % mtd.erasesize) != 0) {
		log_printf(LOG_ERROR, "offset:0x%x is not a sector boundary\n"
			"It needs to be multiples of erasesize:0x%x\n",
			offset, mtd.erasesize);
		ret = -1;
	}

	return ret;
}

/*----------------------------------------------------------------------------
 * @fn flash_erase
 *
 * @brief Erase the content of given SPI NOR device, at given offset and length.
 * @params  dev_fd [IN] - The file descriptor of SPI NOR device to be erased
 * 			offset [IN] - The offset in SPI NOR device to begin erasing
 * 			length [IN] - Number of bytes will be erased
 * @return  0 - Success
 * 			-1 - Failure
 *--------------------------------------------------------------------------*/
static int flash_erase(int dev_fd, ulong offset, ulong length)
{
	int i, blocks;

	erase.start = offset;
	erase.length = (length + mtd.erasesize - 1) / mtd.erasesize;
	erase.length *= mtd.erasesize;

	blocks = erase.length / mtd.erasesize;
	erase.length = mtd.erasesize;

	/* Erasing required flash sector based on input file size */
	for (i = 1; i <= blocks; i++) {
		log_printf(LOG_NORMAL, "\rErasing blocks: %d/%d (%d%%)",
			i, blocks, PERCENTAGE(i, blocks));
		if (ioctl(dev_fd, MEMERASE, &erase) < 0) {
			log_printf(LOG_ERROR,
				"Error While erasing blocks 0x%.8x-0x%.8x: %m\n",
				(unsigned int) erase.start,
				(unsigned int) (erase.start + erase.length));
			return -1;
		}
		erase.start += mtd.erasesize;
	}

	log_printf(LOG_NORMAL, "\rErasing blocks: %d/%d (100%%)\n",
		blocks, blocks);

	return 0;
}

/*----------------------------------------------------------------------------
 * @fn flash_open
 *
 * @brief Open a file with specified flags
 * @params  filename [IN] - Name of file to be opened
 * 			flags [IN] - Flags when opening file
 * @return  File descriptor - Success
 * 			-1 - Failure
 *--------------------------------------------------------------------------*/
static int flash_open(char *filename, int flags)
{
	int fd = -1;

	fd = open(filename, flags);
	if (fd < 0) {
		log_printf(LOG_ERROR, "Failed to open the file: %s\n",
			filename);
	}

	return fd;
}

/*----------------------------------------------------------------------------
 * @fn flash_read
 *
 * @brief Read content from file
 * @params  fd [IN] - File descriptor of file to read from
 * 			filename [IN] - Name of file to be read
 * 			buf [OUT] - Buffer contains read data
 * 			count [IN] - Size to read in bytes
 * @return  0 - Success
 * 			-1 - Failure
 *--------------------------------------------------------------------------*/
static int flash_read(int fd, const char *filename, void *buf, size_t count)
{
	size_t result;
	int ret = 0;

	result = read(fd, buf, count);

	if (count != result) {
		printf("\n");
		if ((signed)result < 0) {
			log_printf(LOG_ERROR, "While reading data from"
				"%s: %m\n", filename);
			ret = -1;
		}
		log_printf(LOG_ERROR, "Short read count returned while reading"
			"from %s\n", filename);
		ret = -1;
	}
	return ret;
}

/*----------------------------------------------------------------------------
 * @fn flash_rewind
 *
 * @brief Rewind pointer to specified offset
 * @params  fd [IN] - File descriptor of file to seek
 * 			offset [IN] - Desired location in file
 * 			filename [IN] - Name of file
 * @return  0 - Success
 * 			-1 - Failure
 *--------------------------------------------------------------------------*/
static int flash_rewind(int fd, unsigned long offset, const char *filename)
{
	if (lseek(fd, offset, SEEK_SET) < 0) {
		log_printf(LOG_ERROR, "While seeking to start of %s: %m\n",
			filename);
		return -1;
	}
	return 0;
}

/*----------------------------------------------------------------------------
 * @fn flash_write
 *
 * @brief Write content of input file to flash at desired offset
 * @params  dev_fd [IN] - File descriptor of flash
 * 			fil_fd [IN] - File descriptor of input file
 * 			offset [IN] - Location in flash to write
 * 			argv1_ptr [IN] - String of input file name
 * 			argv2_ptr [IN] - String of input flash offset
 * @return  0 - Success
 * 			-1 - Failure
 *--------------------------------------------------------------------------*/
static int flash_write(int dev_fd, int fil_fd, ulong offset, char *argv1_ptr,
			 char *argv2_ptr)
{
	ssize_t result, size, written;
	unsigned char src[BUFSIZE];
	int i, tmp = 0;
	int ret = 0;

	log_printf(LOG_NORMAL, "Writing data: 0k/%luk (0%%)",
		KB(filestat.st_size));

	size = filestat.st_size;
	i = BUFSIZE;
	written = 0;

	if (offset) {
		tmp = flash_rewind(dev_fd, offset, argv2_ptr);
		if (tmp < 0) {
			ret = -1;
			goto out;
		}
	}

	/* Writing file content into flash partition */
	while (size) {
		if (size < BUFSIZE)
			i = size;

		log_printf(LOG_NORMAL, "\rWriting data: %dk/%luk (%lu%%)",
			KB(written + i), KB(filestat.st_size),
			PERCENTAGE(written + i, filestat.st_size));

		/* read from filename */
		tmp = flash_read(fil_fd, argv1_ptr, src, i);
		if (tmp < 0) {
			ret = -1;
			goto out;
		}

		/* write to device */
		result = write(dev_fd, src, i);
		if (i != result) {
			printf("\n");
			if (result < 0) {
				log_printf(LOG_ERROR, "While writing data to"
					"0x%.8x-0x%.8x on %s: %m\n",
					written, written + i, argv2_ptr);
				ret = -1;
				goto out;
			}

			log_printf(LOG_ERROR, "Short write count returned while"
				"writing to x%.8x-0x%.8x on %s: %d/%lu bytes"
				" written to flash\n", written, written + i,
				argv2_ptr, written + result, filestat.st_size);
			ret = -1;
			goto out;
		}
		written += i;
		size -= i;
	}

	log_printf(LOG_NORMAL,
		"\rWriting data: %luk/%luk (100%%)\n",
		KB(filestat.st_size), KB(filestat.st_size));
out:
	return ret;
}

/*----------------------------------------------------------------------------
 * @fn flash_verify
 *
 * @brief Compare input file with flashed content
 * @params  dev_fd [IN] - File descriptor of flash
 * 			fil_fd [IN] - File descriptor of input file
 * 			offset [IN] - Location in flash to start comparing
 * 			argv1_ptr [IN] - String of input file name
 * 			argv2_ptr [IN] - String of input flash offset
 * @return  0 - Match
 * 			-1 - Failure
 *--------------------------------------------------------------------------*/
static int flash_verify(int dev_fd, int fil_fd, ulong offset, char *argv1_ptr,
			char *argv2_ptr)
{
	unsigned char src[BUFSIZE], dest[BUFSIZE];
	ssize_t size, written;
	int i, tmp = 0;
	int ret = 0;

	/*
	 * After flash write operation file pointer and device pointer
	 * are moved to other location based upon the size. For verify
	 * operation we need to move back the file poitner and device
	 * pointer to original location
	 */
	tmp = flash_rewind(fil_fd, 0x0, argv1_ptr);
	if (tmp < 0) {
		ret = -1;
		goto out;
	}

	tmp = flash_rewind(dev_fd, offset, argv2_ptr);
	if (tmp < 0) {
		ret = -1;
		goto out;
	}

	size = filestat.st_size;
	i = BUFSIZE;
	written = 0;

	log_printf(LOG_NORMAL, "Verifying data: 0k/%luk (0%%)",
		KB(filestat.st_size));

	/* Verifying flash sector content with input file */
	while (size) {
		if (size < BUFSIZE)
			i = size;

		log_printf(LOG_NORMAL,
			"\rVerifying data: %dk/%luk (%lu%%)", KB(written + i),
			KB(filestat.st_size),
			PERCENTAGE(written + i, filestat.st_size));

		/* read from filename */
		tmp = flash_read(fil_fd, argv1_ptr, src, i);
		if (tmp < 0) {
			ret = -1;
			goto out;
		}

		/* read from device */
		tmp = flash_read(dev_fd, argv2_ptr, dest, i);
		if (tmp < 0) {
			ret = -1;
			goto out;
		}

		/* compare buffers */
		if (memcmp(src, dest, i)) {
			log_printf(LOG_ERROR, "File does not seem to match"
				"flash data. First mismatch at 0x%.8x-0x%.8x\n",
				written, written + i);
			ret = -1;
			goto out;
		}
		written += i;
		size -= i;
	}

	log_printf(LOG_NORMAL,
		"\rVerifying data: %luk/%luk (100%%)\n",
		KB(filestat.st_size), KB(filestat.st_size));

	log_printf(LOG_NORMAL, "Verified flash content %lu bytes, Success.\n",
		filestat.st_size);

out:
	return ret;
}

/*----------------------------------------------------------------------------
 * @fn crc16
 *
 * @brief Calculate CRC16
 * @params  ptr [IN] - Input buffer to calculate CRC16 on
 * 			count [IN] - Number of bytes to calculate CRC16
 * @return  CRC16 value
 *--------------------------------------------------------------------------*/
static int crc16(uint8_t *ptr, int count)
{
	int crc = 0;
	int i;

	while (--count >= 0) {
		crc = crc ^ (int)*ptr++ << 8;
		for (i = 0; i < 8; ++i) {
			if (crc & 0x8000)
				crc = crc << 1 ^ 0x1021;
			else
				crc = crc << 1;
		}
	}

	return crc & 0xffff;
}

/*----------------------------------------------------------------------------
 * @fn nvparam_get
 *
 * @brief Read one erase block of NVPARAM
 * @params  mtd_fd [IN] - File descriptor of SPI NOR device contains NVPARAM partition
 * 			nvparam_base [IN] - Base offset of NVPARAM partition
 * 			nvparam_blob [OUT] - Buffer of read NVPARAM
 * @return  0 - Success
 * 			-1 - Failure
 *--------------------------------------------------------------------------*/
static int nvparam_get(int mtd_fd, ulong nvparam_base, void *nvparam_blob)
{
	int ret = -1;

	ret = flash_rewind(mtd_fd, nvparam_base, NULL);
	if (ret < 0)
		return ret;

	ret = flash_read(mtd_fd, NULL, nvparam_blob, mtd.erasesize);

	return ret;
}

/*----------------------------------------------------------------------------
 * @fn help
 *
 * @brief Display help for this application
 * @params  name [IN] - Application name
 *--------------------------------------------------------------------------*/
static void help(char *name)
{
	log_printf(LOG_NORMAL, "%s -f <file> -o <SPI offset>: "
			"Write binary <file> to host SPI NOR at offset <SPI offset>."
			"\n\t<file> is NVPARAM blob generated by NV-parameter generator.\n"
			"%s -s <value> -o <SPI offset>: "
			"Set a single NVPARAM at host SPI NOR offset <SPI offset> to <value>\n"
			"%s -r -o <SPI offset>: "
			"Read a single NVPARAM at host SPI NOR offset <SPI offset> and display\n"
			"%s -c -o <NVPARAM base SPI offset>: "
			"Clear NVPARAM partition at <NVPARAM base SPI offset>\n"
			"%s -d <file> -o <NVPARAM base SPI offset>: "
			"Dump a NVPARAM partition at <NVPARAM base SPI offset> to binary file <file>\n"
			"%s -l -o <NVPARAM base SPI offset>: "
			"\n\tList out all the valid nvparam settings on a NVPARAM partition at <NVPARAM base SPI offset>\n"
			"%s -e -o <SPI offset>: "
			"Erase a particular NVPARAM at host SPI NOR offset <SPI offset>\n"
			"%s -h: "
			"Print this help\n", name, name, name, name, name, name, name, name);
}

int main(int argc, char *argv[])
{
	static int dev_fd = -1, fil_fd = -1;
	int ret = 0;
	int nMTDDeviceNumber= -1;
	unsigned long offset = ULONG_MAX, value = ULONG_MAX;
	char *argv_dev_ptr;
	char mtd_dev[16];
	char temp_mtd[4] = {0}, *temp;
	FILE *proc_fp;
	char proc_buf[80];
	int argflag;
	int options_used[MAX_OPTIONS] = { 0, 0, 0, 0, 0, 0, 0, 0, 0 }; /* c, d, f, h, o, r, s, l, e */
	char *filepath = NULL;
	char *input_offset = NULL;
	char *input_value = NULL;

	if (argc == 1) {
		help(argv[0]);
		goto exit_free;
	}
	while ((argflag = getopt(argc, (char **)argv, OPTION_STRING)) != -1) {
		switch (argflag) {
		case 'c':
			options_used[OPTION_C] = 1;
			break;
		case 'd':
		case 'f':
			if (argflag == 'd')
				options_used[OPTION_D] = 1;
			else
				options_used[OPTION_F] = 1;
			if (filepath) {
				free(filepath);
				filepath = NULL;
			}
			filepath = strdup(optarg);
			if (!filepath) {
				log_printf(LOG_ERROR, "Option -%c: malloc failure\n", argflag);
				ret = 1;
				goto exit_free;
			}
			break;
		case 'o':
			options_used[OPTION_O] = 1;
			if (input_offset) {
				free(input_offset);
				input_offset = NULL;
			}
			input_offset = strdup(optarg);
			if (!input_offset) {
				log_printf(LOG_ERROR, "Option -o: malloc failure\n");
				ret = 1;
				goto exit_free;
			}
			break;
		case 'r':
			options_used[OPTION_R] = 1;
			break;
		case 's':
			options_used[OPTION_S] = 1;
			if (input_value) {
				free(input_value);
				input_value = NULL;
			}
			input_value = strdup(optarg);
			if (!input_value) {
				log_printf(LOG_ERROR, "Option -s: malloc failure\n");
				ret = 1;
				goto exit_free;
			}
			break;
		case 'l':
			options_used[OPTION_L] = 1;
			break;
		case 'e':
			options_used[OPTION_E] = 1;
			break;
		case 'h':
			options_used[OPTION_H] = 1;
		default:
			help(argv[0]);
			goto exit_free;
			break;
		}
	}

	/* Sanitize user inputs */
	if (!options_used[OPTION_O]) {
		log_printf(LOG_ERROR, "SPI offset must be specified\n");
		help(argv[0]);
		ret = 1;
		goto exit_free;
	}

	if ((options_used[OPTION_C] && options_used[OPTION_D])
			|| (options_used[OPTION_C] && options_used[OPTION_F])
			|| (options_used[OPTION_C] && options_used[OPTION_R])
			|| (options_used[OPTION_C] && options_used[OPTION_S])
			|| (options_used[OPTION_C] && options_used[OPTION_L])
			|| (options_used[OPTION_C] && options_used[OPTION_E])
			|| (options_used[OPTION_D] && options_used[OPTION_R])
			|| (options_used[OPTION_D] && options_used[OPTION_F])
			|| (options_used[OPTION_D] && options_used[OPTION_S])
			|| (options_used[OPTION_D] && options_used[OPTION_L])
			|| (options_used[OPTION_D] && options_used[OPTION_E])
			|| (options_used[OPTION_S] && options_used[OPTION_F])
			|| (options_used[OPTION_S] && options_used[OPTION_R])
			|| (options_used[OPTION_S] && options_used[OPTION_L])
			|| (options_used[OPTION_S] && options_used[OPTION_E])
			|| (options_used[OPTION_F] && options_used[OPTION_R])
			|| (options_used[OPTION_F] && options_used[OPTION_L])
			|| (options_used[OPTION_F] && options_used[OPTION_E])
			|| (options_used[OPTION_R] && options_used[OPTION_L])
			|| (options_used[OPTION_R] && options_used[OPTION_E])
			|| (options_used[OPTION_L] && options_used[OPTION_E])) {
		log_printf(LOG_ERROR,
				"Options -c, -d, -f, -r, -s, -l, -e can't be mixed together.\n");
		help(argv[0]);
		ret = 1;
		goto exit_free;
	}

	if (input_offset) {
		offset = strtoul(input_offset, NULL, 16);
		if ((offset == ULONG_MAX) && (errno == ERANGE)) {
			log_printf(LOG_ERROR, "Input %s is %s\n", input_offset, strerror(errno));
			ret = 1;
			goto exit_free;
		}
	}

	if (input_value) {
		value = strtoul(input_value, NULL, 16);
		if ((value == ULONG_MAX) && (errno == ERANGE)) {
			log_printf(LOG_ERROR, "Input %s is %s\n", input_offset, strerror(errno));
			ret = 1;
			goto exit_free;
		}
	}

	/*
	 * TODO: If the SPI-NOR device does not probed. Need to rescan HOST
	 * SPI-NOR to probe the device
	 */

	/* Finding the MTD partition for host boot SPI chip */
	if ((proc_fp = fopen(PROC_MTD_INFO, "r")) == NULL)
	{
		log_printf(LOG_ERROR, "Unable to open %s to get MTD info...\n", PROC_MTD_INFO);
		goto out;
	}

	while (fgets(proc_buf, sizeof(proc_buf), proc_fp) != NULL)
	{
		/* Try to find "HOST SPI" for BIOS flash */
		if (strstr(proc_buf, HOST_SPI_FLASH_MTD_NAME))
		{
			temp  = strtok(proc_buf, ":");
			if(temp == NULL)
			{
				log_printf(LOG_ERROR,"Error in finding the BIOS Partition \n");
				fclose(proc_fp);
				goto out;
			}

			memcpy((char *)&temp_mtd, (char *)&temp[3], (strlen(temp) - 3));
			nMTDDeviceNumber = atoi(temp_mtd);
		}
	}
	fclose(proc_fp);

	if (nMTDDeviceNumber == -1)
	{
		log_printf(LOG_ERROR,"Unable to find HOST SPI / PNOR  MTD partition...\n");
		goto out;
	}
	ret= snprintf(&mtd_dev[0], MTD_DEV_SIZE, "/dev/mtd%d", nMTDDeviceNumber);
	if(ret  >= MTD_DEV_SIZE || ret < 0)
	{
		log_printf(LOG_ERROR,"Buffer Overflow.\n");
		goto out;
	}

	argv_dev_ptr = &mtd_dev[0];

	dev_fd  = flash_open(argv_dev_ptr, O_SYNC | O_RDWR);
	if (dev_fd < 0){
		ret = 1;
		goto out;
	}

	/* Get the MTD device info */
	ret = ioctl(dev_fd, MEMGETINFO, &mtd);
	if (ret < 0)
		goto out;

	/* Process user inputs */
	if (options_used[OPTION_C]) {
		if (validate_input_offset(offset) < 0) {
			ret = 1;
			goto out;
		}

		if (flash_erase(dev_fd, offset, NVPARAM_PARTITION_SIZE) < 0) {
			log_printf(LOG_ERROR, "Failed to erase\n");
			ret = 1;
			goto out;
		}
	}

	if (options_used[OPTION_D]) {
		struct nvparam_entry blob[mtd.erasesize / sizeof(struct nvparam_entry)];
		ulong nvparam_base = (offset / mtd.erasesize) * mtd.erasesize;

		if (validate_input_offset(offset) < 0) {
			ret = 1;
			goto out;
		}

		if (nvparam_get(dev_fd, nvparam_base, (void *) &blob) < 0) {
			log_printf(LOG_ERROR, "Failed to read NVPARAM\n");
			ret = 1;
			goto out;
		}

		fil_fd = flash_open(filepath, O_CREAT|O_WRONLY);
		if (fil_fd < 0) {
			ret = 1;
			goto out;
		}

		if (write(fil_fd, &blob, sizeof(blob)) < 0) {
			log_printf(LOG_ERROR, "Failed to write NVPARAM to file\n");
			ret = 1;
			goto out;
		}
	}

	if (options_used[OPTION_F]) {
		fil_fd = flash_open(filepath, O_RDONLY);
		if (fil_fd < 0) {
			ret = 1;
			goto out;
		}
		if (validate_input_file(fil_fd, filepath) < 0) {
			ret = 1;
			goto out;
		}
		if (validate_input_offset(offset) < 0) {
			ret = 1;
			goto out;
		}

		if (flash_erase(dev_fd, offset, filestat.st_size) < 0) {
			log_printf(LOG_ERROR, "Failed to erase\n");
			ret = 1;
			goto out;
		}
		if (flash_write(dev_fd, fil_fd, offset, filepath, input_offset) < 0) {
			ret = 1;
			goto out;
		}
		if (flash_verify(dev_fd, fil_fd, offset, filepath, input_offset) < 0) {
			ret = 1;
			goto out;
		}
	}

	if (options_used[OPTION_S]) {
		struct nvparam_entry blob[mtd.erasesize / sizeof(struct nvparam_entry)];
		ulong nvparam_base = (offset / mtd.erasesize) * mtd.erasesize;
		uint entry_no = (offset - nvparam_base) / sizeof(struct nvparam_entry);

		if (nvparam_get(dev_fd, nvparam_base, (void *) &blob) < 0) {
			log_printf(LOG_ERROR, "Failed to read NVPARAM\n");
			ret = 1;
			goto out;
		}

		blob[entry_no].param1 = value;
		/*
		 * Set acl_rd, acl_wr and valid to all 1s as requirement of
		 * host firmware team.
		 */
		blob[entry_no].acl_rd = 0xFF;
		blob[entry_no].acl_wr = 0x7F;
		blob[entry_no].valid = 1;
		/*
		 * Calculate NVPRAM entry CRC16. CRC16 is calculated on whole NVPARAM
		 * entry with crc16 = 0
		 */
		blob[entry_no].crc16 = 0;
		blob[entry_no].crc16 = crc16((uint8_t *) &blob[entry_no],
				sizeof(struct nvparam_entry));

		if (flash_erase(dev_fd, nvparam_base, sizeof(blob)) < 0) {
			log_printf(LOG_ERROR, "Failed to erase\n");
			ret = 1;
			goto out;
		}

		ret = flash_rewind(dev_fd, nvparam_base, NULL);
		if (ret < 0) {
			ret = 1;
			goto out;
		}

		ret = write(dev_fd, (void *)blob, sizeof(blob));
		if (ret < 0) {
			ret = 1;
			goto out;
		}
	}

	if (options_used[OPTION_E]) {
		struct nvparam_entry blob[mtd.erasesize / sizeof(struct nvparam_entry)];
		ulong nvparam_base = (offset / mtd.erasesize) * mtd.erasesize;
		uint entry_no = (offset - nvparam_base) / sizeof(struct nvparam_entry);

		if (nvparam_get(dev_fd, nvparam_base, (void *) &blob) < 0) {
			log_printf(LOG_ERROR, "Failed to read NVPARAM\n");
			ret = 1;
			goto out;
		}

		memset((void *) &blob[entry_no], 0xFF, sizeof(struct nvparam_entry));

		if (flash_erase(dev_fd, nvparam_base, sizeof(blob)) < 0) {
			log_printf(LOG_ERROR, "Failed to erase\n");
			ret = 1;
			goto out;
		}

		ret = flash_rewind(dev_fd, nvparam_base, NULL);
		if (ret < 0) {
			ret = 1;
			goto out;
		}

		ret = write(dev_fd, (void *) blob, sizeof(blob));
		if (ret < 0) {
			ret = 1;
			goto out;
		}
	}

	if(options_used[OPTION_L]) {
		struct nvparam_entry blob[mtd.erasesize / sizeof(struct nvparam_entry)];
		ulong nvparam_base = (offset / mtd.erasesize) * mtd.erasesize;
		int tmp_crc16, cal_crc16 = 0, found_records = 0;
		uint index, max_items = mtd.erasesize / sizeof(struct nvparam_entry);

		if (nvparam_get(dev_fd, nvparam_base, (void *) &blob) < 0) {
			log_printf(LOG_ERROR, "Failed to read NVPARAM\n");
			ret = 1;
			goto out;
		}

		/* List out all valid NVPARAM (CRC field is identical with calculated CRC) */
		for(index = 0; index < max_items; index++)
		{
			tmp_crc16 = blob[index].crc16;
			blob[index].crc16 = 0;
			cal_crc16 = crc16((uint8_t *) &blob[index],
					sizeof(struct nvparam_entry));
			if(tmp_crc16 == cal_crc16)
			{
				log_printf(LOG_NORMAL, "NVPARAM at 0x%x: 0x%08x (ACL_RD:0x%x - ACL_WR:0x%x)\n",
					nvparam_base + (index * sizeof(struct nvparam_entry)), blob[index].param1,
					blob[index].acl_rd, blob[index].acl_wr);
				found_records++;
			}
		}
		log_printf(LOG_NORMAL, "Found total %d valid record(s) from Offset: %x\n",
							found_records, offset);
	}

	if (options_used[OPTION_R]) {
		struct nvparam_entry blob[mtd.erasesize / sizeof(struct nvparam_entry)];
		ulong nvparam_base = (offset / mtd.erasesize) * mtd.erasesize;
		uint entry_no = (offset - nvparam_base) / sizeof(struct nvparam_entry);
		int tmp_crc16, cal_crc16 = 0;

		if (nvparam_get(dev_fd, nvparam_base, (void *) &blob) < 0) {
			log_printf(LOG_ERROR, "Failed to read NVPARAM\n");
			ret = 1;
			goto out;
		}

		log_printf(LOG_NORMAL, "NVPARAM at 0x%x:\n"
				"\tParam: 0x%08x\n"
				"\tACL_RD: 0x%02x\n"
				"\tACL_WR: 0x%02x\n"
				"\tValid: %d\n"
				"\tCRC16: %x",
				offset, blob[entry_no].param1, blob[entry_no].acl_rd,
				blob[entry_no].acl_wr, blob[entry_no].valid,
				blob[entry_no].crc16);
		tmp_crc16 = blob[entry_no].crc16;
		/*
		 * Calculate NVPRAM entry CRC16. CRC16 is calculated on whole NVPARAM
		 * entry with crc16 = 0
		 */
		blob[entry_no].crc16 = 0;
		cal_crc16 = crc16((uint8_t *) &blob[entry_no], sizeof(struct nvparam_entry));
		if (tmp_crc16 != cal_crc16)
			log_printf(LOG_NORMAL, " (Mismatch! Calculated CRC16: %x)\n", cal_crc16);
		else
			log_printf(LOG_NORMAL, "\n");
	}

out:
	close(dev_fd);
	close(fil_fd);
exit_free:
	if (filepath) {
		free(filepath);
		filepath = NULL;
	}
	if (input_offset) {
		free(input_offset);
		input_offset = NULL;
	}
	if (input_value) {
		free(input_value);
		input_value = NULL;
	}

	return ret;
}
