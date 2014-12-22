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

	Graph = snap.LoadEdgeList(snap.PNGraph, g, 0, 1)

	NF = [0]*(h+1) #diameter=8 plus extra first line


	it = Graph.BegNI()
	for i in range (0,Graph.GetNodes()):
		v = it.GetId()
		
		SrcNId = v
		DistNbrsV = snap.TIntFltKdV()
		snap.GetAnf(Graph, SrcNId, DistNbrsV, h, True, 64)

		#print len(DistNbrsV) prints 2 + diameter
		#for i=1 to h, prints (i, nbrhood_function_v(i))
		j = 0
		for item in DistNbrsV:
		    NF[j] = NF[j] + item.Dat()
		    j = j + 1

		it.Next()
		#print DistNbrsV[len(DistNbrsV)-1].Key() to get nbrh(src, min{diameter, h})

	print 'Output:'
	for i in range(0,h+1):
		print NF[i]
	

	elapsed = time.time() - start_time
	print elapsed

if __name__ == "__main__":
	g = "twitter-directed.txt"
	Graph = snap.LoadEdgeList(snap.PNGraph, g, 0, 1)
	#UGraph = snap.LoadEdgeList(snap.PUNGraph, g, 0, 1)
	h = snap.GetBfsFullDiam(Graph, 100)
	getANF(g,h)