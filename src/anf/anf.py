"""
Edward M. Liu (eml2170)
Sakhar AlKhereyf
COMS 6998 - Social Networks
Final Project ANF code
Dec 21, 2014
"""

#!/usr/bin/env python
import sys
import snap
import time

def getANF(g, h):
	start_time = time.time()

	UGraph = snap.LoadEdgeList(snap.PUNGraph, g, 0, 1)

	NF = [0]*9 #diameter=8 plus extra first line

	for v in range(0,UGraph.GetNodes()):
		SrcNId = v
		DistNbrsV = snap.TIntFltKdV()
		snap.GetAnf(UGraph, SrcNId, DistNbrsV, 8, False, 64)

		#print len(DistNbrsV) prints 2 + diameter
		#for i=1 to h, prints (i, nbrhood_function_v(i))
		i = 0
		for item in DistNbrsV:
		    NF[i] = NF[i] + item.Dat()
		    i = i + 1

		#print DistNbrsV[len(DistNbrsV)-1].Key() to get nbrh(src, min{diameter, h})

	print 'Output:'
	for i in range(0,9):
		print NF[i]


	elapsed = time.time() - start_time
	print elapsed

if __name__ == "__main__":
	g = "facebook_combined.txt"
	UGraph = snap.LoadEdgeList(snap.PUNGraph, g, 0, 1)
	h = snap.GetBfsFullDiam(UGraph, 100)
	getANF(g,h)