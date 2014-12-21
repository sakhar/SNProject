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

UGraph = snap.LoadEdgeList(snap.PUNGraph, "facebook_combined.txt", 0, 1)


SrcNId = 0
DistNbrsV = snap.TIntFltKdV()
snap.GetAnf(UGraph, SrcNId, DistNbrsV, 3, False, 64)

#print len(DistNbrsV) prints 2 + diameter
#for i=1 to h, prints (i, nbrhood_function_v(i))
for item in DistNbrsV:
    print item.Key(), item.Dat()

#print DistNbrsV[len(DistNbrsV)-1].Key() to get nbrh(src, min{diameter, h})


"""
UGraph = snap.LoadEdgeList(snap.PUNGraph, sys.argv[1], 0, 1)
#DistNbrsV = snap.TIntFltKdV()

def getBFSDiameter():
	input = sys.argv[1]
	#UGraph = snap.LoadEdgeList(snap.PUNGraph, input, 0, 1)
	print snap.GetBfsFullDiam(UGraph, 100) #What is the second argument?

def nf_v(src, h):
	#UGraph = snap.LoadEdgeList(snap.PUNGraph, sys.argv[1], 0, 1)
	UGraph = snap.LoadEdgeList(snap.PUNGraph, sys.argv[1], 0, 1)
	avg = 0
	iterations = 10
	for i in range(0, iterations):
		print 'hello'
		
		DistNbrsV = snap.TIntFltKdV()
		snap.GetAnf(UGraph, src, DistNbrsV, h, False, 64)


		output_length = len(DistNbrsV) - 2
		print 'output_length=', output_length
		if output_length < h:
			iterations = iterations - 1
			continue
		avg = avg + DistNbrsV[len(DistNbrsV)-1].Dat()
	avg = avg / iterations
	return avg


def nf(h):
	#UGraph = snap.LoadEdgeList(snap.PUNGraph, sys.argv[1], 0, 1)
	total = 0
	for v in range(0, UGraph.GetNodes()):
		total = total + nf_v(v, h)
	return total

if __name__ == "__main__":
    getBFSDiameter()
    #print nf_v(0, 2)
    #print nf(3)
"""
