#!/bin/sh

########################################################################
########################################################################
##
## Tripwire(R) 2.3 for LINUX(R) Post-RPM installation script
##
## Copyleft information contained in footer
##
########################################################################
########################################################################

##=======================================================
## Setup
##=======================================================

# We can assume all the correct tools are in place because the
# RPM installed, didn't it?

##-------------------------------------------------------
## Set HOST_NAME variable
##-------------------------------------------------------
HOST_NAME='localhost'
if uname -n > /dev/null 2> /dev/null ; then
	HOST_NAME=`uname -n`
fi

##-------------------------------------------------------
## Program variables - edited by RPM during initial install
##-------------------------------------------------------

# Site Passphrase variable
TW_SITE_PASS="tripwire"

# Complete path to site key
SITE_KEY="/etc/tripwire/site.key"

# Local Passphrase variable
TW_LOCAL_PASS="tripwire"

# Complete path to local key
LOCAL_KEY="/etc/tripwire/${HOST_NAME}-local.key"

# If clobber==true, overwrite files; if false, do not overwrite files.
CLOBBER="false"

# If prompt==true, ask for confirmation before continuing with install.
PROMPT="true"

# Name of twadmin executeable
TWADMIN="twadmin"

# Path to twadmin executeable
TWADMPATH=/usr/sbin

# Path to configuration directory
CONF_PATH="/etc/tripwire"

# Name of clear text policy file
TXT_POL=$CONF_PATH/twpol.txt

# Name of clear text configuration file
TXT_CFG=$CONF_PATH/twcfg.txt

# Name of encrypted configuration file
CONFIG_FILE=$CONF_PATH/tw.cfg

# Path of the final Tripwire policy file (signed)
SIGNED_POL=`grep POLFILE $TXT_CFG | sed -e 's/^.*=\(.*\)/\1/'`


##=======================================================
## Create Key Files
##=======================================================

##-------------------------------------------------------
## If user has to enter a passphrase, give some
## advice about what is appropriate.
##-------------------------------------------------------

if [ -z "$TW_SITE_PASS" ] || [ -z "$TW_LOCAL_PASS" ]; then
cat << END_OF_TEXT

----------------------------------------------
The Tripwire site and local passphrases are used to
sign a variety of files, such as the configuration,
policy, and database files.

Passphrases should be at least 8 characters in length
and contain both letters and numbers.

See the Tripwire manual for more information.
END_OF_TEXT
fi

##=======================================================
## Generate keys.
##=======================================================

echo
echo "----------------------------------------------"
echo "Creating key files..."

##-------------------------------------------------------
## Site key file.
##-------------------------------------------------------

# If clobber is true, and prompting is off (unattended operation)
# and the key file already exists, remove it.  Otherwise twadmin
# will prompt with an "are you sure?" message.

if [ "$CLOBBER" = "true" ] && [ "$PROMPT" = "false" ] && [ -f "$SITE_KEY" ] ; then
        rm -f "$SITE_KEY"
fi

if [ -f "$SITE_KEY" ] && [ "$CLOBBER" = "false" ] ; then
	echo "The site key file \"$SITE_KEY\""
	echo 'exists and will not be overwritten.'
else
	cmdargs="--generate-keys --site-keyfile \"$SITE_KEY\""
	if [ -n "$TW_SITE_PASS" ] ; then
		cmdargs="$cmdargs --site-passphrase \"$TW_SITE_PASS\""
     	fi
	eval "\"$TWADMPATH/$TWADMIN\" $cmdargs"
	if [ $? -ne 0 ] ; then
		echo "Error: site key generation failed"
		exit 1
        else chmod 640 "$SITE_KEY"
	fi
fi

##-------------------------------------------------------
## Local key file.
##-------------------------------------------------------

# If clobber is true, and prompting is off (unattended operation)
# and the key file already exists, remove it.  Otherwise twadmin
# will prompt with an "are you sure?" message.

if [ "$CLOBBER" = "true" ] && [ "$PROMPT" = "false" ] && [ -f "$LOCAL_KEY" ] ; then
        rm -f "$LOCAL_KEY"
fi

if [ -f "$LOCAL_KEY" ] && [ "$CLOBBER" = "false" ] ; then
	echo "The site key file \"$LOCAL_KEY\""
	echo 'exists and will not be overwritten.'
else
	cmdargs="--generate-keys --local-keyfile \"$LOCAL_KEY\""
	if [ -n "$TW_LOCAL_PASS" ] ; then
		cmdargs="$cmdargs --local-passphrase \"$TW_LOCAL_PASS\""
        fi
	eval "\"$TWADMPATH/$TWADMIN\" $cmdargs"
	if [ $? -ne 0 ] ; then
		echo "Error: local key generation failed"
		exit 1
        else chmod 640 "$LOCAL_KEY"
	fi
fi

##=======================================================
## Sign the Configuration File
##=======================================================

echo
echo "----------------------------------------------"
echo "Signing configuration file..."

##-------------------------------------------------------
## If noclobber, then backup any existing config file.
##-------------------------------------------------------

if [ "$CLOBBER" = "false" ] && [ -s "$CONFIG_FILE" ] ; then
	backup="${CONFIG_FILE}.$$.bak"
	echo "Backing up $CONFIG_FILE"
	echo "        to $backup"
	`mv "$CONFIG_FILE" "$backup"`
	if [ $? -ne 0 ] ; then
		echo "Error: backup of configuration file failed."
		exit 1
	fi
fi

##-------------------------------------------------------
## Build command line.
##-------------------------------------------------------

cmdargs="--create-cfgfile"
cmdargs="$cmdargs --cfgfile \"$CONFIG_FILE\""
cmdargs="$cmdargs --site-keyfile \"$SITE_KEY\""
if [ -n "$TW_SITE_PASS" ] ; then
	cmdargs="$cmdargs --site-passphrase \"$TW_SITE_PASS\""
fi

##-------------------------------------------------------
## Sign the file.
##-------------------------------------------------------

eval "\"$TWADMPATH/$TWADMIN\" $cmdargs \"$TXT_CFG\""
if [ $? -ne 0 ] ; then
	echo "Error: signing of configuration file failed."
	exit 1
fi

# Set the rights properly
chmod 640 "$CONFIG_FILE"

##-------------------------------------------------------
## We keep the cleartext version around.
##-------------------------------------------------------

cat << END_OF_TEXT

A clear-text version of the Tripwire configuration file
$TXT_CFG
has been preserved for your inspection.  It is recommended
that you delete this file manually after you have examined it.

END_OF_TEXT

##=======================================================
## Sign tripwire policy file.
##=======================================================

echo
echo "----------------------------------------------"
echo "Signing policy file..."

##-------------------------------------------------------
## If noclobber, then backup any existing policy file.
##-------------------------------------------------------

if [ "$CLOBBER" = "false" ] && [ -s "$POLICY_FILE" ] ; then
	backup="${POLICY_FILE}.$$.bak"
	echo "Backing up $POLICY_FILE"
	echo "        to $backup"
	mv "$POLICY_FILE" "$backup"
	if [ $? -ne 0 ] ; then
		echo "Error: backup of policy file failed."
		exit 1
	fi
fi

##-------------------------------------------------------
## Build command line.
##-------------------------------------------------------

cmdargs="--create-polfile"
cmdargs="$cmdargs --cfgfile \"$CONFIG_FILE\""
cmdargs="$cmdargs --site-keyfile \"$SITE_KEY\""
if [ -n "$TW_SITE_PASS" ] ; then
	cmdargs="$cmdargs --site-passphrase \"$TW_SITE_PASS\""
fi

##-------------------------------------------------------
## Sign the file.
##-------------------------------------------------------

eval "\"$TWADMPATH/$TWADMIN\" $cmdargs \"$TXT_POL\""
if [ $? -ne 0 ] ; then
	echo "Error: signing of policy file failed."
	exit 1
fi

# Set the proper rights on the newly signed policy file.
chmod 0640 "$SIGNED_POL"

##-------------------------------------------------------
## We keep the cleartext version around.
##-------------------------------------------------------

cat << END_OF_TEXT

A clear-text version of the Tripwire policy file
$TXT_POL
has been preserved for your inspection.  This implements
a minimal policy, intended only to test essential
Tripwire functionality.  You should edit the policy file
to describe your system, and then use twadmin to generate
a new signed copy of the Tripwire policy.

END_OF_TEXT

# Initialize tripwire database
/usr/sbin/tripwire --init --cfgfile $CONFIG_FILE --site-keyfile $SITE_KEY \
--local-passphrase $TW_LOCAL_PASS 2> /dev/null

########################################################################
########################################################################
#
#                        TRIPWIRE GPL NOTICES
#
# The developer of the original code and/or files is Tripwire, Inc.
# Portions created by Tripwire, Inc. are copyright 2000 Tripwire, Inc.
# Tripwire is a registered trademark of Tripwire, Inc.  All rights reserved.
#
# This program is free software.  The contents of this file are subject to
# the terms of the GNU General Public License as published by the Free
# Software Foundation; either version 2 of the License, or (at your option)
# any later version.  You may redistribute it and/or modify it only in
# compliance with the GNU General Public License.
#
# This program is distributed in the hope that it will be useful.  However,
# this program is distributed "AS-IS" WITHOUT ANY WARRANTY; INCLUDING THE
# IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
# Please see the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
#
# Nothing in the GNU General Public License or any other license to use the
# code or files shall permit you to use Tripwire's trademarks,
# service marks, or other intellectual property without Tripwire's
# prior written consent.
#
# If you have any questions, please contact Tripwire, Inc. at either
# info@tripwire.org or www.tripwire.org.
#
########################################################################
########################################################################
