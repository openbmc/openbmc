package Bastille::API::Miscellaneous;
use strict;

use File::Path;
use Bastille::API;
use Bastille::API::HPSpecific;
use Bastille::API::FileContent;

require Exporter;
our @ISA = qw(Exporter);
our @EXPORT_OK = qw(
PrepareToRun
B_is_package_installed
);
our @EXPORT = @EXPORT_OK;


###########################################################################
#
# PrepareToRun sets up Bastille to run.  It checks the ARGV array for
# special options and runs ConfigureForDistro to set necessary file
# locations and other global variables.
#
###########################################################################

sub PrepareToRun {

    # Make sure we're root!
    if ( $> != 0 ) {
	&B_log("ERROR","Bastille must run as root!\n");
        exit(1);
    }


    # Make any directories that don't exist...
    foreach my $dir (keys %GLOBAL_BDIR) {
        my $BdirPath = $GLOBAL_BDIR{$dir};
        if ( $BdirPath =~ /^\s*\// ) { #Don't make relative directories
            mkpath ($BdirPath,0,0700);
        }
    }

    if(&GetDistro =~ "^HP-UX") {
	&B_check_system;
    }

    &B_log("ACTION","\n########################################################\n" .
	       "#  Begin Bastille Run                                  #\n" .
	       "########################################################\n\n");

    #read sum file if it exists.
    &B_read_sums;


# No longer necessary as flags are no longer in sum file, and sums are
# are now checked "real time"

    # check the integrity of the files listed
#    for my $file (sort keys %GLOBAL_SUM) {
#	&B_check_sum($file);
#    }
    # write out the newly flagged sums
#    &B_write_sums;


}



###########################################################################
# &B_is_package_installed($package);
#
# This function checks for the existence of the package named.
#
# TODO: Allow $package to be an expression.
# TODO: Allow optional $version, $release, $epoch arguments so we can
#       make sure that the given package is at least as recent as some
#       given version number.
#
# scalar return values:
# 0:    $package is not installed
# 1:    $package is installed
###########################################################################

sub B_is_package_installed($) {
    no strict;
    my $package = $_[0];
# Create a "global" variable with values scoped to this function
# We do this to avoid having to repeatedly swlist/rpm
# when we run B_is_package_installed
local %INSTALLED_PACKAGE_LIST;

    my $distro = &GetDistro;
    if ($distro =~ /^HP-UX/) {
        if (&checkProcsForService('swagent','ignore_warning') == SECURE_CANT_CHANGE()) {
            &B_log("WARNING","Software Distributor Agent(swagent) is not running.  Can not tell ".
                   "if package: $package is installed or not.  Bastille will assume not.  ".
                   "If the package is actually installed, Bastille may report or configure incorrectly.".
                   "To use Bastille-results as-is, please check to ensure $package is not installed, ".
                   "or re-run with the swagent running to get correct results.");
            return 0; #FALSE
        }
	my $swlist=&getGlobal('BIN','swlist');
        if (%INSTALLED_PACKAGE_LIST == () ) { # re-use prior results
          if (open(SWLIST, "$swlist -a state -l fileset |")) {
            while (my $line = <SWLIST>){
              if ($line =~ /^ {2}\S+\.(\S+)\s*(\w+)/) {
                $INSTALLED_PACKAGE_LIST{$1} = $2;
              }
            }
          close SWLIST;
          } else {
            &B_log("ERROR","B_is_package_installed was unable to run the swlist command: $swlist,\n");
            return FALSE;
          }
        }
        # Now find the entry
        if ($INSTALLED_PACKAGE_LIST{$package} == 'configured') {
            return TRUE;
        } else {
            return FALSE;
        }
    } #End HP-UX Section
    # This routine only works on RPM-based distros: Red Hat, Fedora, Mandrake and SuSE
    elsif ( ($distro !~ /^RH/) and ($distro !~ /^MN/) and($distro !~ /^SE/) ) {
        return 0;
    } else { #This is a RPM-based distro
        # Run an rpm command -- librpm is extremely messy, dynamic and not
        # so much a perl thing.  It's actually barely a C/C++ thing...
        if (open RPM,"rpm -q $package") {
            # We should get only one line back, but let's parse a few
            # just in case.
            my @lines = <RPM>;
            close RPM;
            #
            # This is what we're trying to parse:
            # $ rpm -q jay
            # package jay is not installed
            # $ rpm -q bash
            # bash-2.05b-305.1
            #

            foreach $line (@lines) {
                if ($line =~ /^package\s$package\sis\snot\sinstalled/) {
            	return 0;
                }
                elsif ($line =~ /^$package\-/) {
            	return 1;
                }
            }

            # If we've read every line without finding one of these, then
            # our parsing is broken
            &B_log("ERROR","B_is_package_installed was unable to find a definitive RPM present or not present line.\n");
            return 0;
        } else {
            &B_log("ERROR","B_is_package_installed was unable to run the RPM command,\n");
            return 0;
        }
    }
}



1;

