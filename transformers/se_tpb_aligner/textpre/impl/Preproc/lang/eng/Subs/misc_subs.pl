#!/usr/bin/perl -w


#***************************************************************#
#	5 digits in pcounter					#
#***************************************************************#
sub format_pcounter {
	my $pcounter = shift;

	$pcounter =~ s/^(\d)$/0000$1/;
	$pcounter =~ s/^(\d\d)$/000$1/;
	$pcounter =~ s/^(\d\d\d)$/00$1/;
	$pcounter =~ s/^(\d\d\d\d)$/0$1/;
	
	return $pcounter;
}
#***************************************************************#
#	Clean blanks
#***************************************************************#
sub clean_blanks {
	my $str = shift;

	$str =~ s/^ +//;
	$str =~ s/ +$//;
	$str =~ s/ +/ /g;
	
	return $str;
}
#***************************************************************#
1;
