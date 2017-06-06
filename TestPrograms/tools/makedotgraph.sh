#/bin/sh
#This script was created by Murray Cole

echo "digraph DataFlow {" > $1.gv

grep -v 'format\|-1\|ATS' $1 | grep DF | awk '{printf "%s -> %s\n", $2, $3}' >> $1.gv

echo "label=\"Dataflow\"; labelloc=top; labeljust=left;" >> $1.gv

echo "}" >> $1.gv

echo "digraph Calls {" >> $1.gv

grep -v 'format\|ATS' $1 | grep BC | awk '{printf "%s -> %s\n", $2, $4}' >> $1.gv

echo "label=\"Calls\"; labelloc=top; labeljust=left;" >> $1.gv

echo "}" >> $1.gv



