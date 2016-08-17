#define _GNU_SOURCE
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <stdarg.h>
#include <stdlib.h>
#include <ctype.h>
#include <fcntl.h>
#include <dirent.h>
#include <unistd.h>
#include <time.h>
#include <getopt.h>
#include <libgen.h>
#include <sys/types.h>
#include <sys/stat.h>

#define MINORBITS	8
#define MKDEV(ma,mi)	(((ma) << MINORBITS) | (mi))
#define MAX_ID_LEN      40
#define MAX_NAME_LEN    40
#ifndef PATH_MAX
#define PATH_MAX        4096
#endif
#define VERSION         "1.0.1"

/* These are all stolen from busybox's libbb to make
 * error handling simpler (and since I maintain busybox,
 * I'm rather partial to these for error handling).
 *  -Erik
 */
static const char *const app_name = "makedevs";
static const char *const memory_exhausted = "memory exhausted";
static char default_rootdir[]=".";
static char *rootdir = default_rootdir;
static int trace = 0;

struct name_id {
	char name[MAX_NAME_LEN+1];
	unsigned long id;
	struct name_id *next;
};

static struct name_id *usr_list = NULL;
static struct name_id *grp_list = NULL;

static void verror_msg(const char *s, va_list p)
{
	fflush(stdout);
	fprintf(stderr, "%s: ", app_name);
	vfprintf(stderr, s, p);
}

static void error_msg_and_die(const char *s, ...)
{
	va_list p;

	va_start(p, s);
	verror_msg(s, p);
	va_end(p);
	putc('\n', stderr);
	exit(EXIT_FAILURE);
}

static void vperror_msg(const char *s, va_list p)
{
	int err = errno;

	if (s == 0)
		s = "";
	verror_msg(s, p);
	if (*s)
		s = ": ";
	fprintf(stderr, "%s%s\n", s, strerror(err));
}

static void perror_msg_and_die(const char *s, ...)
{
	va_list p;

	va_start(p, s);
	vperror_msg(s, p);
	va_end(p);
	exit(EXIT_FAILURE);
}

static FILE *xfopen(const char *path, const char *mode)
{
	FILE *fp;

	if ((fp = fopen(path, mode)) == NULL)
		perror_msg_and_die("%s", path);
	return fp;
}

static char *xstrdup(const char *s)
{
	char *t;

	if (s == NULL)
		return NULL;

	t = strdup(s);

	if (t == NULL)
		error_msg_and_die(memory_exhausted);

	return t;
}

static struct name_id* alloc_node(void)
{
	struct name_id *node;
	node = (struct name_id*)malloc(sizeof(struct name_id));
	if (node == NULL) {
		error_msg_and_die(memory_exhausted);
	}
	memset((void *)node->name, 0, MAX_NAME_LEN+1);
	node->id = 0xffffffff;
	node->next = NULL;
	return node;
}

static struct name_id* parse_line(char *line)
{
	char *p;
	int i;
	char id_buf[MAX_ID_LEN+1];
	struct name_id *node;
	node = alloc_node();
	p = line;
	i = 0;
	// Get name field
	while (*p != ':') {
		if (i > MAX_NAME_LEN)
			error_msg_and_die("Name field too long");
		node->name[i++] = *p++;
	}
	node->name[i] = '\0';
	p++;
	// Skip the second field
	while (*p != ':')
		p++;
	p++;
	// Get id field
	i = 0;
	while (*p != ':') {
		if (i > MAX_ID_LEN)
			error_msg_and_die("ID filed too long");
		id_buf[i++] = *p++;
	}
	id_buf[i] = '\0';
	node->id = atol(id_buf);
	return node;
}

static void get_list_from_file(FILE *file, struct name_id **plist)
{
	char *line;
	int len = 0;
	size_t length = 256;
	struct name_id *node, *cur;

	if((line = (char *)malloc(length)) == NULL) {
		error_msg_and_die(memory_exhausted);
	}

	while ((len = getline(&line, &length, file)) != -1) {
		node = parse_line(line);
		if (*plist == NULL) {
			*plist = node;
			cur = *plist;
		} else {
			cur->next = node;
			cur = cur->next;
		}
	}

	if (line)
		free(line);
}

static unsigned long convert2guid(char *id_buf, struct name_id *search_list)
{
	char *p;
	int isnum;
	struct name_id *node;
	p = id_buf;
	isnum = 1;
	while (*p != '\0') {
		if (!isdigit(*p)) {
			isnum = 0;
			break;
		}
		p++;
	}
	if (isnum) {
		// Check for bad user/group name
		node = search_list;
		while (node != NULL) {
			if (!strncmp(node->name, id_buf, strlen(id_buf))) {
				fprintf(stderr, "WARNING: Bad user/group name %s detected\n", id_buf);
				break;
			}
			node = node->next;
		}
		return (unsigned long)atol(id_buf);
	} else {
		node = search_list;
		while (node != NULL) {
			if (!strncmp(node->name, id_buf, strlen(id_buf)))
				return node->id;
			node = node->next;
		}
		error_msg_and_die("No entry for %s in search list", id_buf);
	}
}

static void free_list(struct name_id *list)
{
	struct name_id *cur;
	cur = list;
	while (cur != NULL) {
		list = cur;
		cur = cur->next;
		free(list);
	}
}

static void add_new_directory(char *name, char *path,
		unsigned long uid, unsigned long gid, unsigned long mode)
{
	if (trace)
		fprintf(stderr, "Directory: %s %s  UID: %ld  GID %ld  MODE: %04lo", path, name, uid, gid, mode);

	if (mkdir(path, mode) < 0) {
		if (EEXIST == errno) {
			/* Unconditionally apply the mode setting to the existing directory.
			 * XXX should output something when trace */
			chmod(path, mode & ~S_IFMT);
		}
	}
	if (trace)
		putc('\n', stderr);
	chown(path, uid, gid);
}

static void add_new_device(char *name, char *path, unsigned long uid,
	unsigned long gid, unsigned long mode, dev_t rdev)
{
	int status;
	struct stat sb;

	if (trace) {
		fprintf(stderr, "Device: %s %s  UID: %ld  GID: %ld  MODE: %04lo  MAJOR: %d  MINOR: %d",
				path, name, uid, gid, mode, (short)(rdev >> 8), (short)(rdev & 0xff));
	}

	memset(&sb, 0, sizeof(struct stat));
	status = lstat(path, &sb);
	if (status >= 0) {
		/* It is ok for some types of files to not exit on disk (such as
		 * device nodes), but if they _do_ exist, the file type bits had
		 * better match those of the actual file or strange things will happen... */
		if ((mode & S_IFMT) != (sb.st_mode & S_IFMT)) {
			if (trace)
				putc('\n', stderr);
			error_msg_and_die("%s: existing file (04%o) type does not match specified file type (04%lo)!",
						path, (sb.st_mode & S_IFMT), (mode & S_IFMT));
		}
		if (mode != sb.st_mode) {
			if (trace)
				fprintf(stderr, " -- applying new mode 04%lo (old was 04%o)\n", mode & ~S_IFMT, sb.st_mode & ~S_IFMT);
			/* Apply the mode setting to the existing device node */
			chmod(path, mode & ~S_IFMT);
		}
		else {
			if (trace)
				fprintf(stderr, " -- extraneous entry in table\n", path);
		}
	}
	else {
		mknod(path, mode, rdev);
		if (trace)
			putc('\n', stderr);

	}

	chown(path, uid, gid);
}

static void add_new_file(char *name, char *path, unsigned long uid,
				  unsigned long gid, unsigned long mode)
{
	if (trace) {
		fprintf(stderr, "File: %s %s  UID: %ld  GID: %ld  MODE: %04lo\n",
			path, name, gid, uid, mode);
	}

	int fd = open(path,O_CREAT | O_WRONLY, mode);
	if (fd < 0) {
		error_msg_and_die("%s: file can not be created!", path);
	} else {
		close(fd);
	}
	chmod(path, mode);
	chown(path, uid, gid);
}


static void add_new_fifo(char *name, char *path, unsigned long uid,
				  unsigned long gid, unsigned long mode)
{
	if (trace) {
		printf("Fifo: %s %s  UID: %ld  GID: %ld  MODE: %04lo\n",
			path, name, gid, uid, mode);
	}

	int status;
	struct stat sb;

	memset(&sb, 0, sizeof(struct stat));
	status = stat(path, &sb);


	/* Update the mode if we exist and are a fifo already */
	if (status >= 0 && S_ISFIFO(sb.st_mode)) {
		chmod(path, mode);
	} else {
		if (mknod(path, mode, 0))
			error_msg_and_die("%s: file can not be created with mknod!", path);
	}
	chown(path, uid, gid);
}


/*  device table entries take the form of:
    <path>	<type> <mode>	<usr>	<grp>	<major>	<minor>	<start>	<inc>	<count>
    /dev/mem    c      640      0       0       1       1       0        0        -
    /dev/zero   c      644      root    root    1       5       -        -        -

    type can be one of:
	f	A regular file
	d	Directory
	c	Character special device file
	b	Block special device file
	p	Fifo (named pipe)

    I don't bother with symlinks (permissions are irrelevant), hard
    links (special cases of regular files), or sockets (why bother).

    Regular files must exist in the target root directory.  If a char,
    block, fifo, or directory does not exist, it will be created.
*/
static int interpret_table_entry(char *line)
{
	char *name;
	char usr_buf[MAX_ID_LEN];
	char grp_buf[MAX_ID_LEN];
	char path[4096], type;
	unsigned long mode = 0755, uid = 0, gid = 0, major = 0, minor = 0;
	unsigned long start = 0, increment = 1, count = 0;

	if (0 > sscanf(line, "%40s %c %lo %40s %40s %lu %lu %lu %lu %lu", path,
		    &type, &mode, usr_buf, grp_buf, &major, &minor, &start,
		    &increment, &count))
	{
		fprintf(stderr, "%s: sscanf returned < 0 for line '%s'\n", app_name, line);
		return 1;
	}

	uid = convert2guid(usr_buf, usr_list);
	gid = convert2guid(grp_buf, grp_list);

	if (strncmp(path, "/", 1)) {
		error_msg_and_die("Device table entries require absolute paths");
	}
	name = xstrdup(path + 1);
	/* prefix path with rootdir */
	sprintf(path, "%s/%s", rootdir, name);

	/* XXX Why is name passed into all of the add_new_*() routines? */
	switch (type) {
	case 'd':
		mode |= S_IFDIR;
		add_new_directory(name, path, uid, gid, mode);
		break;
	case 'f':
		mode |= S_IFREG;
		add_new_file(name, path, uid, gid, mode);
		break;
	case 'p':
		mode |= S_IFIFO;
		add_new_fifo(name, path, uid, gid, mode);
		break;
	case 'c':
	case 'b':
		mode |= (type == 'c') ? S_IFCHR : S_IFBLK;
		if (count > 0) {
			int i;
			dev_t rdev;
			char buf[80];

			for (i = start; i < start + count; i++) {
				sprintf(buf, "%s%d", name, i);
				sprintf(path, "%s/%s%d", rootdir, name, i);
				/* FIXME:  MKDEV uses illicit insider knowledge of kernel
				 * major/minor representation...  */
				rdev = MKDEV(major, minor + (i - start) * increment);
				sprintf(path, "%s/%s\0", rootdir, buf);
				add_new_device(buf, path, uid, gid, mode, rdev);
			}
		} else {
			/* FIXME:  MKDEV uses illicit insider knowledge of kernel
			 * major/minor representation...  */
			dev_t rdev = MKDEV(major, minor);
			add_new_device(name, path, uid, gid, mode, rdev);
		}
		break;
	default:
		error_msg_and_die("Unsupported file type");
	}
	if (name) free(name);
	return 0;
}


static void parse_device_table(FILE * file)
{
	char *line;
	size_t length = 256;
	int len = 0;

	if((line = (char *)malloc(length)) == NULL) {
		error_msg_and_die(memory_exhausted);
	}
	/* Looks ok so far.  The general plan now is to read in one
	 * line at a time, trim off leading and trailing whitespace,
	 * check for leading comment delimiters ('#') or a blank line,
	 * then try and parse the line as a device table entry. If we fail
	 * to parse things, try and help the poor fool to fix their
	 * device table with a useful error msg... */

	while ((len = getline(&line, &length, file)) != -1) {
		/* First trim off any whitespace */

		/* trim trailing whitespace */
		while (len > 0 && isspace(line[len - 1]))
			line[--len] = '\0';

		/* trim leading whitespace */
		memmove(line, &line[strspn(line, " \n\r\t\v")], len + 1);

		/* If this is NOT a comment or an empty line, try to interpret it */
		if (*line != '#' && *line != '\0') interpret_table_entry(line);
	}

	if (line)
		free(line);
}

static int parse_devtable(FILE * devtable)
{
	struct stat sb;

	if (lstat(rootdir, &sb)) {
		perror_msg_and_die("%s", rootdir);
	}
	if (chdir(rootdir))
		perror_msg_and_die("%s", rootdir);

	if (devtable)
		parse_device_table(devtable);

	return 0;
}


static struct option long_options[] = {
	{"root", 1, NULL, 'r'},
	{"help", 0, NULL, 'h'},
	{"trace", 0, NULL, 't'},
	{"version", 0, NULL, 'v'},
	{"devtable", 1, NULL, 'D'},
	{NULL, 0, NULL, 0}
};

static char *helptext =
	"Usage: makedevs [OPTIONS]\n"
	"Build entries based upon device_table.txt\n\n"
	"Options:\n"
	"  -r, -d, --root=DIR     Build filesystem from directory DIR (default: cwd)\n"
	"  -D, --devtable=FILE    Use the named FILE as a device table file\n"
	"  -h, --help             Display this help text\n"
	"  -t, --trace            Be verbose\n"
	"  -v, --version          Display version information\n\n";


int main(int argc, char **argv)
{
	int c, opt;
	extern char *optarg;
	struct stat statbuf;
	char passwd_path[PATH_MAX];
	char group_path[PATH_MAX];
	FILE *passwd_file = NULL;
	FILE *group_file = NULL;
	FILE *devtable = NULL;
	DIR *dir = NULL;

	umask (0);

	if (argc==1) {
		fprintf(stderr, helptext);
		exit(1);
	}

	while ((opt = getopt_long(argc, argv, "D:d:r:htv",
			long_options, &c)) >= 0) {
		switch (opt) {
		case 'D':
			devtable = xfopen(optarg, "r");
			if (fstat(fileno(devtable), &statbuf) < 0)
				perror_msg_and_die(optarg);
			if (statbuf.st_size < 10)
				error_msg_and_die("%s: not a proper device table file", optarg);
			break;
		case 'h':
			printf(helptext);
			exit(0);
		case 'r':
		case 'd':				/* for compatibility with mkfs.jffs, genext2fs, etc... */
			if (rootdir != default_rootdir) {
				error_msg_and_die("root directory specified more than once");
			}
			if ((dir = opendir(optarg)) == NULL) {
				perror_msg_and_die(optarg);
			} else {
				closedir(dir);
			}
			/* If "/" is specified, use "" because rootdir is always prepended to a
			 * string that starts with "/" */
			if (0 == strcmp(optarg, "/"))
				rootdir = xstrdup("");
			else
				rootdir = xstrdup(optarg);
			break;

		case 't':
			trace = 1;
			break;

		case 'v':
			printf("%s: %s\n", app_name, VERSION);
			exit(0);
		default:
			fprintf(stderr, helptext);
			exit(1);
		}
	}

	if (argv[optind] != NULL) {
		fprintf(stderr, helptext);
		exit(1);
	}

	// Get name-id mapping
	sprintf(passwd_path, "%s/etc/passwd", rootdir);
	sprintf(group_path, "%s/etc/group", rootdir);
	if ((passwd_file = fopen(passwd_path, "r")) != NULL) {
		get_list_from_file(passwd_file, &usr_list);
		fclose(passwd_file);
	}
	if ((group_file = fopen(group_path, "r")) != NULL) {
		get_list_from_file(group_file, &grp_list);
		fclose(group_file);
	}

	// Parse devtable
	if(devtable) {
		parse_devtable(devtable);
		fclose(devtable);
	}

	// Free list
	free_list(usr_list);
	free_list(grp_list);

	return 0;
}
