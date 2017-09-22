import sys
import csv
import math
import time
import heapq
import random
import datetime
import numpy as np
import statsmodels.api as sm

AIC = []
BIC = []
R2A = []
fitness = []
pfitness = []
elites = []

Fitm = 0
Fitt = 0
Mut = 0
Sel = 0
Cross = 0


def type(x):
	for i in x:
		sys.stdout.write(i)
		sys.stdout.flush()
		#0.035		
		time.sleep(0.0)
	print("")
	return ""


#generate random bitstring of length x
def randbin(x):
	y = (2 ** x) - 1
 	b = bin(random.randint(0, y))
	return b[2:].rjust(x, '0') 


def fitmodels(x):
	#Fit Models
	s = datetime.datetime.now()	
	global AIC
	global BIC
	global R2A 
	
	del AIC[:]
	del BIC[:]
	del R2A[:]
	#Loop over population
	#For every individual, decode and transform columns
	
	for i in range(0, population):
		transf = []
		for j in range(0,totalbits,5):
			varnum = int(j / 5)
			e = expon(pop[i][j+2:j+5])
			if(str(pop[i][j:j+2]) == "01"):		#x^exp
				transf.append(pol(varnum, e))
			if(str(pop[i][j:j+2]) == "10"):
				transf.append(l(varnum, e))	#ln(x)^exp
			if(str(pop[i][j:j+2]) == "11"):
				transf.append(eu(varnum, e))	#e^(x)^exp
		#Fit Model Using Transformed Variables
		y = map(lambda x: float(x), responses)
		fit1 = fit(y, transf)
		#print(fit1.summary())
	
		#Store Fitness Criteria For Each Model
		AIC.append(fit1.aic)
		BIC.append(fit1.bic)
		R2A.append(fit1.rsquared_adj)

	f = datetime.datetime.now()
	global Fitm
	Fitm = (f - s)
	return ""

#indexes of largest [::-1] or smallest [::1] elements of a list
def indec(a,n):
	return np.argsort(a)[::1][:n]


def fitness():
	#Fitness Function

	s = datetime.datetime.now()
		

	#Custom Fitness Function avg(AIC, BIC) + (1 - adj R^2)	
	#ff = []
	#for i in range(0, population):
	#	t = (((AIC[i] + BIC[i])/2.0) + (1 - R2A[i])) 
	#	ff.append(t)	
	
	fitness = np.array(AIC)
	bestfit = AIC.index(min(AIC))

	global elites
	del elites[:]
	elitest = indec(fitness,(elite*2))

	for i in range(0,(elite*2)):	

		elites.append(pop[elitest[i]])

	
	print("\nBest model this generation, with Fitness = " + str(fitness[bestfit]))

	printsol(bestfit)
	printcoef(bestfit)
	

	#calculate proportional fitness of each individual	
	
	global pfitness
	pfitness = []	
	totalfit = sum(fitness)

	for i in range(0, population):
		pfitness.append((fitness[i]/totalfit))	


	f = datetime.datetime.now()
	global Fitt
	Fitt = (f - s)
	return ""


def SelectParent():
	p = 0
	ran = random.uniform(0, 1)
	for i in range(0,population):
		p = p + pfitness[i]
		if(ran < p):
			return i


def select():
	#Selection
	s = datetime.datetime.now()
	selectnum = int(population/2)									# number of selections to perform, always half the population
	a, b = [], []											# parent arrays a and b for each selection

	#Uniform											# each individual equally likely to be chosen regardless of fitness
	if(selection == "uniform"):
		for i in range(0,selectnum):
			a.append(random.randint(0,(population - 1)))
			b.append(random.randint(0,(population - 1)))					# can select same individual twice
	
	#Proportional											# individuals selected based on proportional fitness
	if(selection == "proportional"):
		for l in range(0,selectnum):
			p1 = SelectParent()
			p2 = p1		
			while(p1 == p2):
				p2 = SelectParent()
			a.append(p1)
			b.append(p2)


	f = datetime.datetime.now()
	global Sel
	Sel = (f - s)
	mutation(crossover(a,b,selectnum))
	return ""



def crossover(a,b,c):
	#Crossover											# each selection has a 0.8 probability to mate, otherwise children = parents
	s = datetime.datetime.now()
	children = []
	#Single Point Crossover
	if(crosstype == "single"):
		for i in range(0,(c - elite)):
			if(random.random() < crossprob):			
				xpoint = random.randint(0,(totalbits - 1))				# choose random bit at which to perform crossover
				children.append(pop[a[i]][0:xpoint] + pop[b[i]][xpoint:totalbits])
				children.append(pop[b[i]][0:xpoint] + pop[a[i]][xpoint:totalbits])
			else:
				children.append(pop[a[i]])
				children.append(pop[b[i]])

	#Uniform Crossover
	if(crosstype == "uniform"):
		for i in range(0,(c - elite)):
			c1 = ""
			c2 = ""
			for j in range(0,totalbits):
				if(random.random() < crossprob):
					c1 = c1 + pop[b[i]][j]
					c2 = c2 + pop[a[i]][j]
				else:
					c1 = c1 + pop[a[i]][j]
					c2 = c2 + pop[b[i]][j]
			#print(str(len(c1)) + "\t"+ str(len(c2)))
			children.append(str(c1))
			children.append(str(c2))					


	#print("\nNumber of children " + str(len(children)))
	f = datetime.datetime.now()
	global Cross
	Cross = (f - s)
	return children


def mutation(x):
	#Mutation												# computationally expensive?
	
	s = datetime.datetime.now()
	n = (population - (elite * 2))

	for i in range(0,n):									
		for j in range(0,totalbits):
			if(random.random() < mutate):
				if(x[i][j] == "0"):
					x[i] = x[i][0:j] + "1" + x[i][(j + 1):totalbits]			# strings immutable in python
				elif(x[i][j] == "1"):
					x[i] = x[i][0:j] + "0" + x[i][(j + 1):totalbits]

			
	#replace previous generation with the children
	global pop
	del pop[:]
	for i in range(0,n):
		pop.append(x[i])

	for i in range(0,len(elites)):
		pop.append(elites[i])

	f = datetime.datetime.now()
	global Mut
	Mut = (f - s)
	return ""


# x^(exp)
def pol(v,e):
	c = []
	te = float(float(e) / 2.0)
	for i in range(0,obs):
		#take the absolute value of the observations
		ci = math.fabs(float(pred[v][i]))
		#avoid 0^neg, in which case set to 0
		if(ci == 0.0 and te < 0.0):
			c.append(0.0)
		else:
			ci = math.pow(ci, te)
			c.append(ci)
	return c


# (e^(x))^(exp)
def eu(v,e):
	ecap = 0
	c = []
	te = float(float(e)/2.0)
	for i in range(0,obs):
		ci = math.fabs(float(pred[v][i]))
		#cap excessively large values at 9 x 10^128
		if(ci > 99.0):
			ecap = ecap + 1
			ci = math.exp(99.0)
			c.append(ci)
		elif(ci < 100.0):
			ci = math.exp(ci)
			if(ci == 0.0 and te < 0.0):
				c.append(0.0)
			else:
				ci = math.pow(ci, te)
				c.append(ci)
	#print("Number of e caps " + str(ecap))
	return c


# ln(x)^(exp)
def l(v,e):
	c = []
	te = float(float(e)/2.0)
	for i in range(0,obs):
		ci = math.fabs(float(pred[v][i]))
		ci = math.log1p(ci)
		if(ci == 0.0 and te < 0.0):
			c.append(0.0)
		else:
			ci = math.pow(ci,te)
			c.append(ci)
	return c


#decode exponent
def expon(x):
    return {
        '000': -6,
        '001': -4,
        '010': -2,
        '011': -1,
        '100': 1,
        '101': 2,
        '110': 4,
        '111': 6,
    }[x]	


#fit model
def fit(y,x):
	ones = np.ones(len(x[0]))
	X = sm.add_constant(np.column_stack((x[0],ones)))
	for e in x[1:]:
		X = sm.add_constant(np.column_stack((e,X)))
	m = sm.OLS(y,X).fit()
	return m


#print solution
def printsol(x):
	#print bitstring separated by variables
	#for i in range(0,totalbits):
	#	print(str(pop[0][i]), end='')
	#	if((i+1) % 5 == 0):
	#		print("\t", end='')
	print("")
	print(response + ' =\n')
	
	solution = []
	nump = 0

	for i in range(0, totalbits, 5):
		solution.append(pop[x][i:i+5])

	for i in range(0,len(solution)):
		e = float(expon(solution[i][2:5]) / 2.0)
		if(str(solution[i][0:2]) == "01"):
			print("(" + predname[i] + ")^(" + str(e) + ") +")
			nump += 1
		if(str(solution[i][0:2]) == "10"):
			print("ln(" + predname[i] + ")^(" + str(e) + ") +")
			nump += 1
		if(str(solution[i][0:2]) == "11"):
			print("(e^" + predname[i] + ")^(" + str(e) + ") +")
			nump += 1
			
	print("\n\n" + str(nump) + " predictors used for solution " + str(x) + ".")
	return 0


def printcoef(x):
	transf = []
	for j in range(0,totalbits,5):
		varnum = int(j / 5)
		e = expon(pop[x][j+2:j+5])
		if(str(pop[x][j:j+2]) == "01"):		#x^exp
			transf.append(pol(varnum, e))
		if(str(pop[x][j:j+2]) == "10"):
			transf.append(l(varnum, e))	#ln(x)^exp
		if(str(pop[x][j:j+2]) == "11"):
			transf.append(eu(varnum, e))	#e^(x)^exp
	#Fit Model Using Transformed Variables
	y = map(lambda x: float(x), responses)
	fit1 = fit(y, transf)
	coef = fit1.params
	t = 1
	print("\nVariable Coefficients: ")	
	for i in range((len(coef) - 1),-1,-1):
		if(i ==(len(coef) - 1)):
			print("Intercept\t%.3f") % coef[i]
		elif(i != (len(coef) - 1)):
			print("X" + str(t) + "\t\t%.3f") % coef[i]
			t +=1
		
	print("")
	print(fit1.summary())
	return ""


	
type("\nPredictive Regression Model Selection Using a Genetic Algorithm")
type("By Matthew Keeran")
type("Design and Analysis of Algorithms: UCF Fall 2016")


#read and parse data

x = raw_input(type("\nPlease enter the directory and filename of csv\nExample: C:\\Users\\John\\Desktop\\Training.csv\n(Assumes there are headers)\n"))

f = csv.reader(open(x, 'r'), delimiter=",", quotechar='|')

#variables (header)
vars = next(f)
columns = len(vars)

#columns
cols = []

for i in range(0,columns):
	cols.append([])

for row in f:
	for i in range(0,columns):
		cols[i].append(row[i])

obs = len(cols[0])
type("\nFound " + str(columns) + " variables with " + str(obs) + " observations.\nVars:")

for i in range(0, columns):
	type("(" + str(i + 1) + ")\t" + vars[i])

response = raw_input(type("\nSelect which variable to use as a response by entering its number \t"))
response = int(response) - 1

responses = cols[response]
pred = []
predname = []
for i in range(0,columns):
	if(i == response):
		continue
	pred.append(cols[i])
	predname.append(vars[i])

response = vars[response]
type("\nYou selected " + response + " as your response.")



#GA parameters

global population
global generations


population = 10					#assumes using only even number
generations = 5
selection = "proportional"				#["proportional", "uniform"]
crosstype = "uniform"				#["uniform", "single"]
crossprob = 0.8
mutate = 0.05
elite = 1

totalpred = columns - 1
bitsper = 5
totalbits = totalpred * bitsper
flag = 0
while (flag == 0):
	type("\nUsing the following GA parameters:\n")
	type("(1)\tGenerations    = " + str(generations))
	type("(2)\tPopulation     = " + str(population))
	type("(3)\tSelection      = " + selection)
	type("(4)\tCrossover      = " + crosstype)
	type("(5)\tCrossover Pr() = " + str(crossprob))
	type("(6)\tMutation Rate  = " + str(mutate))
	type("(7)\tElitism	       = " + str(elite))
	type("\n(0)\tStart GA")

	param = raw_input(type("\nType the number of the parameter to modify or 0 to start the GA"))

	if(param == "0"):
		flag = 1
	elif(param == "1"):
		generations = int(raw_input(type("\nplease enter the number (integer) of generations to perform: ")))
	elif(param == "2"):
		population = int(raw_input(type("\nplease enter the size (even integer) of the population to use: ")))
	elif(param == "3"):
		selection = raw_input(type("\nplease enter the type of the selection to perform (uniform or proportional): "))
	elif(param == "4"):
		crosstype = raw_input(type("\nplease enter the type of crossover to use (single or uniform): "))
	elif(param == "5"):
		crossprob = float(raw_input(type("\nplease enter the probability of crossover (float) to use: ")))
	elif(param == "6"):
		mutate = float(raw_input(type("\nplease enter the rate of mutation (float) to use: ")))		
	elif(param == "7"):
		elite = int(raw_input(type("\nplease enter the number of elites (int) to keep per generation: ")))

start = datetime.datetime.now()

#Start # runs loop

#Create Initial Population

pop = []

for i in range(0, population):
	pop.append(randbin(totalbits))


#start #generations loop
for g in range(0,generations):

	fitmodels(pop)
	fitness()
	select()
	print("\nGeneration " + str(g+1) + " Complete!")


end = datetime.datetime.now()
elapsed = end - start

print('\nTotal time elapsed: ' + str(elapsed))

tots = (Fitm + Fitt + Sel + Cross + Mut)
tots = tots.total_seconds()
Fitm = (Fitm.total_seconds() / tots)
Fitt = (Fitt.total_seconds() / tots)
Sel = (Sel.total_seconds() /tots)
Cross = (Cross.total_seconds() /tots)
Mut = (Mut.total_seconds() / tots)

print('\nRunTime Distribution (%):')
print('Fitting Models\t\t%.3f') % Fitm
print('Fitness Function\t%.3f') % Fitt
print('Selection\t\t%.3f') % Sel
print('Crossover\t\t%.3f') % Cross
print('Mutation\t\t%.3f') % Mut
print("")
