# EEPROMER
#
# Licensed under the GNU General Public License.

EEPROMER_DIR	:= eepromer

EEPROMER_CFLAGS	:= -Wstrict-prototypes -Wshadow -Wpointer-arith -Wcast-qual \
		   -Wcast-align -Wwrite-strings -Wnested-externs -Winline \
		   -W -Wundef -Wmissing-prototypes -Iinclude

EEPROMER_TARGETS	:= eepromer eeprom eeprog

#
# Programs
#

$(EEPROMER_DIR)/eepromer: $(EEPROMER_DIR)/eepromer.o
	$(CC) $(LDFLAGS) -o $@ $^

$(EEPROMER_DIR)/eeprom: $(EEPROMER_DIR)/eeprom.o
	$(CC) $(LDFLAGS) -o $@ $^

$(EEPROMER_DIR)/eeprog: $(EEPROMER_DIR)/eeprog.o $(EEPROMER_DIR)/24cXX.o 
	$(CC) $(LDFLAGS) -o $@ $^

#
# Objects
#

$(EEPROMER_DIR)/eepromer.o: $(EEPROMER_DIR)/eepromer.c
	$(CC) $(CFLAGS) $(EEPROMER_CFLAGS) -c $< -o $@

$(EEPROMER_DIR)/eeprom.o: $(EEPROMER_DIR)/eeprom.c
	$(CC) $(CFLAGS) $(EEPROMER_CFLAGS) -c $< -o $@

$(EEPROMER_DIR)/eeprog.o: $(EEPROMER_DIR)/eeprog.c
	$(CC) $(CFLAGS) $(EEPROMER_CFLAGS) -c $< -o $@

$(EEPROMER_DIR)/24cXX.o: $(EEPROMER_DIR)/24cXX.c
	$(CC) $(CFLAGS) $(EEPROMER_CFLAGS) -c $< -o $@

#
# Commands
#

all-eepromer: $(addprefix $(EEPROMER_DIR)/,$(EEPROMER_TARGETS))

strip-eepromer: $(addprefix $(EEPROMER_DIR)/,$(EEPROMER_TARGETS))
	strip $(addprefix $(EEPROMER_DIR)/,$(EEPROMER_TARGETS))

clean-eepromer:
	$(RM) $(addprefix $(EEPROMER_DIR)/,*.o $(EEPROMER_TARGETS))

install-eepromer: $(addprefix $(EEPROMER_DIR)/,$(EEPROMER_TARGETS))
	$(INSTALL_DIR) $(DESTDIR)$(sbindir) $(DESTDIR)$(man8dir)
	for program in $(EEPROMER_TARGETS) ; do \
	$(INSTALL_PROGRAM) $(EEPROMER_DIR)/$$program $(DESTDIR)$(sbindir) ; done

uninstall-eepromer:
	for program in $(EEPROMER_TARGETS) ; do \
	$(RM) $(DESTDIR)$(sbindir)/$$program ; \
	$(RM) $(DESTDIR)$(man8dir)/$$program.8 ; done

all: all-eepromer

strip: strip-eepromer

clean: clean-eepromer

install: install-eepromer

uninstall: uninstall-eepromer
