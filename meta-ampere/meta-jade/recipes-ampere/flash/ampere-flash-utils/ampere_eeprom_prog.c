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
 * This program is for updating Atmel AT24 EEPROM via BMC I2C bus
 */

#include <fcntl.h>
#include <linux/errno.h>
#include <linux/i2c.h>
#include <linux/i2c-dev.h>
#include <netinet/in.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <time.h>
#include <unistd.h>
#include <zlib.h>

#pragma pack(1)

#define PERCENTAGE(x, total) (((x)*100) / (total))

enum eeprom_type
{
	EEPROM_24C02 = 1,
	EEPROM_24C64 = 2,
	EEPROM_24C512 = 3,
	EEPROM_24C1024 = 4
};

#define DEFAULT_I2C_BUS              1
#define DEFAULT_I2C_EEPROM_ADDR      0x50
#define DEFAULT_I2C_EEPROM_TYPE      EEPROM_24C1024
#define EEPROM_RD_FLG                0
#define EEPROM_WR_FLG                1
#define EEPROM_24C1024_PAGE_SIZE     0x100
#define EEPROM_24C512_PAGE_SIZE      0x80
#define EEPROM_24C64_PAGE_SIZE       0x20
#define EEPROM_24C02_PAGE_SIZE       0x8
#define EEPROM_MAX_PAGE_SIZE_SUPPORT EEPROM_24C1024_PAGE_SIZE
#define MAX_EEPROM_ADDR_LEN          2

struct smpmpro_ctl
{
	uint8_t prog_mode;
	uint8_t detect_mode;
	uint8_t read_mode;
	uint8_t i2c_bus;
	uint8_t eeprom_addr;
	uint8_t eeprom_type;
	char filename[128];
	uint32_t rc;
};

static struct smpmpro_ctl ctl;

static void display_usage(void)
{
	printf("ampere_eeprom_prog\n");
	printf("Usage: ampere_eeprom_prog <args>\n");
	printf("Arguments:\n");
	printf("\t-b\t\t: I2C bus number\n");
	printf("\t-s\t\t: EEPROM I2C address\n");
	printf("\t-t\t\t: EEPROM device type\n");
	printf("\t\t\t 1:24c04 2:24c64 3:24c1024\n");
	printf("\t-r <count>\t: read <count> bytes from EEPROM offset 0\n");
	printf("\t-p\t\t: program the file\n");
	printf("\t-d\t\t: detect the EEPROM\n");
	printf("\t-f <file>\t: The firmware file\n");
}

/*
 * Open the i2c bus device
 */
static int open_i2c_dev(char *i2c_device)
{
	int fd = open(i2c_device, O_RDWR);

	if (fd < 0)
	{
		perror("Failed to open I2C device!");
		return -1;
	}

	return fd;
}

static int bus_arg_handler(int argc, char **argv, int index)
{
	ctl.i2c_bus = (uint8_t)strtol(argv[index + 1], NULL, 10);
	if (ctl.i2c_bus > 32)
	{
		printf("Invalid I2C bus %d\n", ctl.i2c_bus);
		return -EINVAL;
	}
	return 0;
}

static int slave_arg_handler(int argc, char **argv, int index)
{
	ctl.eeprom_addr = (uint8_t)strtol(argv[index + 1], NULL, 16);
	return 0;
}

static int dev_type_arg_handler(int argc, char **argv, int index)
{
	ctl.eeprom_type = (uint8_t)strtol(argv[index + 1], NULL, 10);
	return 0;
}

static int read_arg_handler(int argc, char **argv, int index)
{
	ctl.read_mode = 1;
	ctl.rc = (uint32_t)strtol(argv[index + 1], NULL, 10);
	if (ctl.rc == 0)
	{
		printf("Invalid read count val %d\n", ctl.rc);
		return -EINVAL;
	}
	return 0;
}

static int prog_arg_handler(int argc, char **argv, int index)
{
	ctl.prog_mode = 1;
	return 0;
}

static int file_arg_handler(int argc, char **argv, int index)
{
	memset(&ctl.filename, '\0', sizeof(ctl.filename));
	strcpy((char *)&ctl.filename, argv[index + 1]);
	return 0;
}

static int detect_arg_handler(int argc, char **argv, int index)
{
	ctl.detect_mode = 1;
	return 0;
}

static char *arglist[] =
{
	"-b",
	"-s",
	"-t",
	"-r",
	"-p",
	"-d",
	"-f",
	NULL};

static int (*handlerlist[])(int, char **, int) =
{
	bus_arg_handler,
	slave_arg_handler,
	dev_type_arg_handler,
	read_arg_handler,
	prog_arg_handler,
	detect_arg_handler,
	file_arg_handler,
	NULL};

static void hexdump(char *buf, ssize_t len)
{
	ssize_t i;

	for (i = 0; i < len; i++)
	{
		if ((i % 16) == 0)
			printf("\n%04lx: ", (unsigned long)i);
		printf("%02x ", buf[i]);
	}
	printf("\n");
}

static int parse_arguments(int argc, char **argv)
{
	int i, j, retval;
	if (argc <= 1)
		return -EINVAL;
	for (i = 1; i < argc; i++)
	{
		j = 0;
	arg_list_loop:
		if (arglist[j] == NULL)
			continue;
		if (strcmp(argv[i], arglist[j]) == 0)
		{
			retval = handlerlist[j](argc, argv, i);
			if (retval >= 0)
			{
				i += retval;
			}
			else
			{
				fprintf(stderr, "Invalid argument: %s\n",
						arglist[j]);
				return -EINVAL;
			}
		}
		j++;
		goto arg_list_loop;
	}
	return 0;
}

static int eeprom_get_pagesize(uint8_t type)
{
	int pagesize;

	switch (type)
	{
	case EEPROM_24C02:
		pagesize = EEPROM_24C02_PAGE_SIZE;
		break;
	case EEPROM_24C64:
		pagesize = EEPROM_24C64_PAGE_SIZE;
		break;
	case EEPROM_24C512:
		pagesize = EEPROM_24C512_PAGE_SIZE;
		break;
	case EEPROM_24C1024:
		pagesize = EEPROM_24C1024_PAGE_SIZE;
		break;
	default:
		pagesize = EEPROM_24C02_PAGE_SIZE;
		break;
	}
	return pagesize;
}

static int i2c_master_write(int fd, uint8_t slave, uint8_t *data, size_t count)
{
	int ret = 0;

	if (ioctl(fd, I2C_SLAVE, slave) < 0)
	{
		ret = -ENODEV;
		return ret;
	}

	/* Write the specified data onto the I2C bus */
	ret = write(fd, data, count);
	if (ret != count)
	{
		printf("Failed to write data to I2C bus\n");
		ret = -ENODEV;
		return ret;
	}

	return ret;
}

static int i2c_master_read(int fd, uint8_t slave, uint8_t *wr_data, uint8_t *data,
						   uint16_t offset, size_t data_len)
{
	struct i2c_rdwr_ioctl_data ioctl_data;
	struct i2c_msg i2c_msgs[2];

	if (data_len > EEPROM_MAX_PAGE_SIZE_SUPPORT)
	{
		printf("*** Warning: Sequential read should not exceed %d bytes, \
                        otherwise the read data will be rolled over!\n",
			   EEPROM_MAX_PAGE_SIZE_SUPPORT);
	}

	/* A dummy write operation should be done according to the I2C protocol */
	ioctl_data.nmsgs = 2;
	ioctl_data.msgs = i2c_msgs;
	ioctl_data.msgs[0].len = MAX_EEPROM_ADDR_LEN;
	ioctl_data.msgs[0].addr = slave;
	ioctl_data.msgs[0].flags = 0;
	ioctl_data.msgs[0].buf = wr_data;

	/* Read operation */
	ioctl_data.msgs[1].len = data_len;
	ioctl_data.msgs[1].addr = slave;
	ioctl_data.msgs[1].flags = 1;
	ioctl_data.msgs[1].buf = data;

	if (ioctl(fd, I2C_RDWR, &ioctl_data) < 0)
	{
		printf("Failed to read data from EEPROM @0x%x via i2c!\n", slave);
		return -1;
	}

	return 0;
}

static int detect_eeprom(int fd, struct smpmpro_ctl *ctl)
{
	uint8_t buff[1];
	int ret;

	ret = i2c_master_write(fd, ctl->eeprom_addr, buff, 0);
	if (ret < 0)
		return -EACCES;
	return 0;
}

static ssize_t eeprom_rd_wr(int fd, struct smpmpro_ctl *ctl, uint32_t offset,
							uint8_t *buf, ssize_t size, uint8_t rw_flag)
{
	ssize_t ret, bytes, len;
	int pagesize;
	uint8_t wr_buf[EEPROM_MAX_PAGE_SIZE_SUPPORT + MAX_EEPROM_ADDR_LEN];
	uint8_t rd_buf[EEPROM_MAX_PAGE_SIZE_SUPPORT];
	uint8_t *p = buf;
	uint16_t buf_off, off_tmp;
	uint32_t off;
	uint8_t eeprom_addr;

	len = size;
	pagesize = eeprom_get_pagesize(ctl->eeprom_type);
	off = offset;
loop:
	if (rw_flag == EEPROM_WR_FLG)
		printf("\rPrograming FW file: %d/%d (%d%%)",
			   (int)(size - len), (int)size,
			   (int)PERCENTAGE(size - len, size));
	else
		printf("\rReading from EEPROM: %d/%d (%d%%)",
			   (int)(size - len), (int)size,
			   (int)PERCENTAGE(size - len, size));
	buf_off = 0;

	/*
	 * The slave I2C EEPROM bus addresses start from 0x50 upto 0x53.
	 * Each I2C slave can address a range of 64KB.
	 * Readjust the offset to address a total of 256KB eeprom memory.
	 */

	if (off >= 0x30000)
	{
		off_tmp = (uint16_t)(off - 0x30000);
	}
	else if (off >= 0x20000)
	{
		off_tmp = (uint16_t)(off - 0x20000);
	}
	else if (off >= 0x10000)
	{
		off_tmp = (uint16_t)(off - 0x10000);
	}
	else
		off_tmp = (uint16_t)off;

	eeprom_addr = ctl->eeprom_addr + off / 0x10000;

	/* EEPROM offset address */
	if (pagesize == EEPROM_24C64_PAGE_SIZE ||
		pagesize == EEPROM_24C512_PAGE_SIZE ||
		pagesize == EEPROM_24C1024_PAGE_SIZE)
	{
		wr_buf[buf_off++] = (off_tmp & 0xFF00) >> 8;
		wr_buf[buf_off++] = (off_tmp & 0x00FF);
	}
	else
	{
		wr_buf[buf_off++] = off_tmp & 0x00FF;
	}
	if (len >= pagesize)
		bytes = pagesize;
	else
		bytes = len;
	if (rw_flag == EEPROM_WR_FLG)
	{
		memcpy(&wr_buf[buf_off], p, bytes);
		ret = i2c_master_write(fd, eeprom_addr, wr_buf, bytes + buf_off);
		if (ret == -1)
		{
			printf("%s: fail to send wr data\n", __func__);
			return -EIO;
		}
		/* delay 10ms for the I2C write is done */
		usleep(10 * 1000);
	}
	else
	{
		ret = i2c_master_read(fd, eeprom_addr, wr_buf, rd_buf, buf_off, bytes);
		if (ret == -1)
		{
			printf("%s: fail to read data\n", __func__);
			return -EIO;
		}
		memcpy(p, rd_buf, bytes);
	}
	off += bytes;
	p += bytes;
	len -= bytes;
	if (len > 0)
		goto loop;

	return (int)(size - len);
}

static int program_fw(int fd, struct smpmpro_ctl *ctl, void *buff, ssize_t sz)
{
	char *buf_tmp;
	ssize_t bytes_wr, bytes_rd;
	int ret = 0;
	uint32_t crc32_checksum;

	printf("Programing FW file: 0/%d (0%%)", (int)sz);
	crc32_checksum = crc32(0, (unsigned char *)buff, sz);
	bytes_wr = eeprom_rd_wr(fd, ctl, 0, (uint8_t *)buff,
							sz, EEPROM_WR_FLG);
	if (bytes_wr == -1)
	{
		printf("FAILED\n");
		return -EIO;
	}
	printf("\rPrograming FW file: %d/%d (100%%)\n", (int)sz, (int)sz);
	printf("===== Pgming FW file completed =====\n");

	buf_tmp = malloc(sz);
	if (!buf_tmp)
	{
		printf("Not enough memory\n");
		return -ENOMEM;
	}
	printf("Reading from EEPROM: 0/%d (0%%)", (int)sz);
	bytes_rd = eeprom_rd_wr(fd, ctl, 0, (uint8_t *)buf_tmp,
							sz, EEPROM_RD_FLG);
	if (bytes_rd == -1)
	{
		printf("FAILED\n");
		ret = -EIO;
		goto err;
	}
	printf("\rReading from EEPROM: %d/%d (100%%)\n", (int)sz, (int)sz);
	printf("===== Reading from EEPROM completed =====\n");

	printf("CRC32 checksum calculation ... ");
	if (crc32_checksum != crc32(0, (unsigned char *)buf_tmp, sz))
	{
		printf("FAILED. Try to program again!\n");
		ret = -EAGAIN;
		goto err;
	}
	printf("PASSED\n");

err:
	free(buf_tmp);
	return ret;
}

int main(int argc, char **argv)
{
	FILE *fp;
	int fd;
	char i2cdev[16];
	int ret;
	ssize_t sz;
	char *buf;

	memset(&ctl, 0, sizeof(struct smpmpro_ctl));
	/* Parsing the arguments */
	ret = parse_arguments(argc, argv);
	if (ret < 0)
	{
		printf("syntax error!\n");
		display_usage();
		return 0;
	}
	if (!ctl.i2c_bus)
		ctl.i2c_bus = DEFAULT_I2C_BUS;
	if (!ctl.eeprom_addr)
		ctl.eeprom_addr = DEFAULT_I2C_EEPROM_ADDR;
	if (!ctl.eeprom_type)
		ctl.eeprom_type = DEFAULT_I2C_EEPROM_TYPE;

	/* Create the device file string */
	ret = snprintf(i2cdev, sizeof(i2cdev), "/dev/i2c-%d", ctl.i2c_bus);
	if (ret >= (signed int)sizeof(i2cdev))
		return -EINVAL;

	/* Open I2C bus device */
	fd = open_i2c_dev(i2cdev);
	if (fd < 0)
	{
		printf("Can not open I2C Device %s\n", i2cdev);
		return -ENODEV;
	}

	/* Try to probe the EEPROM */
	printf("Probing %s for the EEPROM 0x%x ... ", i2cdev, ctl.eeprom_addr);
	if (detect_eeprom(fd, &ctl))
	{
		printf("NOT FOUND!\n");
		return -ENODEV;
	}
	printf("FOUND\n");
	if (ctl.detect_mode)
		return 0;

	/* Attemp to read from EEPROM */
	if (ctl.read_mode)
	{
		printf("Reading %d bytes from EEPROM: ... ", ctl.rc);
		sz = 0;
		buf = malloc(ctl.rc);
		if (!buf)
		{
			printf("Not enough memory\n");
			return -ENOMEM;
		}
		sz = eeprom_rd_wr(fd, &ctl, 0, (uint8_t *)buf,
						  ctl.rc, EEPROM_RD_FLG);
		if (sz == -1)
		{
			printf("FAILED\n");
			ret = -EIO;
		}
		ret = 0;
		hexdump((void *)buf, ctl.rc);
		free(buf);
		return ret;
	}

	/* Read FW file to buffer */
	fp = fopen(ctl.filename, "rb");
	if (!fp)
	{
		printf("Can't open file for reading\n");
		return -EINVAL;
	}
	fseek(fp, 0, SEEK_END);
	sz = ftell(fp);
	buf = malloc(sz);
	if (!buf)
	{
		printf("Not enough memory\n");
		return -ENOMEM;
	}
	fseek(fp, 0, SEEK_SET);
	fread(buf, sz, 1, fp);

	if (ctl.prog_mode)
	{
		ret = program_fw(fd, &ctl, buf, sz);
		if (ret)
		{
			free(buf);
			return -EIO;
		}
	}

	free(buf);
	fclose(fp);

	return 0;
}
