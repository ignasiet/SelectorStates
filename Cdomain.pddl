(define (domain wumpus)

(:predicates 
	(at_p1-2)
	(adj_p1-1_p1-2)
	(at_p1-1)
	(alive)
	(safe_p1-2)
	(at_p1-3)
	(adj_p1-1_p1-3)
	(safe_p1-3)
	(at_p2-1)
	(adj_p1-1_p2-1)
	(safe_p2-1)
	(at_p2-2)
	(adj_p1-1_p2-2)
	(safe_p2-2)
	(at_p2-3)
	(adj_p1-1_p2-3)
	(safe_p2-3)
	(at_p3-1)
	(adj_p1-1_p3-1)
	(safe_p3-1)
	(at_p3-2)
	(adj_p1-1_p3-2)
	(safe_p3-2)
	(at_p3-3)
	(adj_p1-1_p3-3)
	(safe_p3-3)
	(adj_p1-2_p1-1)
	(safe_p1-1)
	(adj_p1-2_p1-3)
	(adj_p1-2_p2-1)
	(adj_p1-2_p2-2)
	(adj_p1-2_p2-3)
	(adj_p1-2_p3-1)
	(adj_p1-2_p3-2)
	(adj_p1-2_p3-3)
	(adj_p1-3_p1-1)
	(adj_p1-3_p1-2)
	(adj_p1-3_p2-1)
	(adj_p1-3_p2-2)
	(adj_p1-3_p2-3)
	(adj_p1-3_p3-1)
	(adj_p1-3_p3-2)
	(adj_p1-3_p3-3)
	(adj_p2-1_p1-1)
	(adj_p2-1_p1-2)
	(adj_p2-1_p1-3)
	(adj_p2-1_p2-2)
	(adj_p2-1_p2-3)
	(adj_p2-1_p3-1)
	(adj_p2-1_p3-2)
	(adj_p2-1_p3-3)
	(adj_p2-2_p1-1)
	(adj_p2-2_p1-2)
	(adj_p2-2_p1-3)
	(adj_p2-2_p2-1)
	(adj_p2-2_p2-3)
	(adj_p2-2_p3-1)
	(adj_p2-2_p3-2)
	(adj_p2-2_p3-3)
	(adj_p2-3_p1-1)
	(adj_p2-3_p1-2)
	(adj_p2-3_p1-3)
	(adj_p2-3_p2-1)
	(adj_p2-3_p2-2)
	(adj_p2-3_p3-1)
	(adj_p2-3_p3-2)
	(adj_p2-3_p3-3)
	(adj_p3-1_p1-1)
	(adj_p3-1_p1-2)
	(adj_p3-1_p1-3)
	(adj_p3-1_p2-1)
	(adj_p3-1_p2-2)
	(adj_p3-1_p2-3)
	(adj_p3-1_p3-2)
	(adj_p3-1_p3-3)
	(adj_p3-2_p1-1)
	(adj_p3-2_p1-2)
	(adj_p3-2_p1-3)
	(adj_p3-2_p2-1)
	(adj_p3-2_p2-2)
	(adj_p3-2_p2-3)
	(adj_p3-2_p3-1)
	(adj_p3-2_p3-3)
	(adj_p3-3_p1-1)
	(adj_p3-3_p1-2)
	(adj_p3-3_p1-3)
	(adj_p3-3_p2-1)
	(adj_p3-3_p2-2)
	(adj_p3-3_p2-3)
	(adj_p3-3_p3-1)
	(adj_p3-3_p3-2)
	(stench_p1-1)
	()
	(stench_p1-2)
	(stench_p1-3)
	(stench_p2-1)
	(stench_p2-2)
	(stench_p2-3)
	(stench_p3-1)
	(stench_p3-2)
	(stench_p3-3)
	(breeze_p1-1)
	(breeze_p1-2)
	(breeze_p1-3)
	(breeze_p2-1)
	(breeze_p2-2)
	(breeze_p2-3)
	(breeze_p3-1)
	(breeze_p3-2)
	(breeze_p3-3)
	(got-the-treasure)
	(gold-at_p1-1)
	(gold-at_p1-2)
	(gold-at_p1-3)
	(gold-at_p2-1)
	(gold-at_p2-2)
	(gold-at_p2-3)
	(gold-at_p3-1)
	(gold-at_p3-2)
	(gold-at_p3-3))

(:action move_p1-2_p3-3
:precondition (and (adj_p1-2_p3-3) (at_p1-2) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p1-2)) 
)

(:action move_p1-2_p3-2
:precondition (and (adj_p1-2_p3-2) (at_p1-2) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p1-2)) 
)

(:action move_p1-2_p3-1
:precondition (and (adj_p1-2_p3-1) (at_p1-2) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p1-2)) 
)

(:action move_p1-2_p2-3
:precondition (and (adj_p1-2_p2-3) (at_p1-2) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p1-2)) 
)

(:action move_p1-2_p2-2
:precondition (and (adj_p1-2_p2-2) (at_p1-2) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p1-2)) 
)

(:action move_p1-2_p2-1
:precondition (and (adj_p1-2_p2-1) (at_p1-2) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p1-2)) 
)

(:action move_p3-3_p3-2
:precondition (and (adj_p3-3_p3-2) (at_p3-3) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p3-3)) 
)

(:action move_p3-3_p3-1
:precondition (and (adj_p3-3_p3-1) (at_p3-3) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p3-3)) 
)

(:action move_p1-2_p1-3
:precondition (and (adj_p1-2_p1-3) (at_p1-2) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p1-2)) 
)

(:action move_p1-2_p1-1
:precondition (and (adj_p1-2_p1-1) (at_p1-2) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p1-2)) 
)

(:action move_p3-3_p2-3
:precondition (and (adj_p3-3_p2-3) (at_p3-3) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p3-3)) 
)

(:action move_p3-3_p2-2
:precondition (and (adj_p3-3_p2-2) (at_p3-3) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p3-3)) 
)

(:action move_p3-3_p2-1
:precondition (and (adj_p3-3_p2-1) (at_p3-3) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p3-3)) 
)

(:action move_p3-3_p1-3
:precondition (and (adj_p3-3_p1-3) (at_p3-3) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p3-3)) 
)

(:action move_p3-3_p1-2
:precondition (and (adj_p3-3_p1-2) (at_p3-3) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p3-3)) 
)

(:action move_p3-3_p1-1
:precondition (and (adj_p3-3_p1-1) (at_p3-3) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p3-3)) 
)

(:action move_p1-3_p3-3
:precondition (and (adj_p1-3_p3-3) (at_p1-3) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p1-3)) 
)

(:action move_p1-3_p3-2
:precondition (and (adj_p1-3_p3-2) (at_p1-3) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p1-3)) 
)

(:action move_p1-3_p3-1
:precondition (and (adj_p1-3_p3-1) (at_p1-3) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p1-3)) 
)

(:action move_p1-3_p2-3
:precondition (and (adj_p1-3_p2-3) (at_p1-3) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p1-3)) 
)

(:action move_p1-3_p2-2
:precondition (and (adj_p1-3_p2-2) (at_p1-3) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p1-3)) 
)

(:action move_p1-3_p2-1
:precondition (and (adj_p1-3_p2-1) (at_p1-3) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p1-3)) 
)

(:action move_p1-3_p1-2
:precondition (and (adj_p1-3_p1-2) (at_p1-3) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p1-3)) 
)

(:action move_p1-3_p1-1
:precondition (and (adj_p1-3_p1-1) (at_p1-3) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p1-3)) 
)

(:action grab_p3-3
:precondition (and (at_p3-3) (gold-at_p3-3) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p3-3)) 
)

(:action grab_p3-2
:precondition (and (at_p3-2) (gold-at_p3-2) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p3-2)) 
)

(:action move_p2-1_p3-3
:precondition (and (adj_p2-1_p3-3) (at_p2-1) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p2-1)) 
)

(:action grab_p3-1
:precondition (and (at_p3-1) (gold-at_p3-1) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p3-1)) 
)

(:action move_p2-1_p3-2
:precondition (and (adj_p2-1_p3-2) (at_p2-1) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p2-1)) 
)

(:action move_p2-1_p3-1
:precondition (and (adj_p2-1_p3-1) (at_p2-1) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p2-1)) 
)

(:action grab_p2-3
:precondition (and (at_p2-3) (gold-at_p2-3) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p2-3)) 
)

(:action grab_p2-2
:precondition (and (at_p2-2) (gold-at_p2-2) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p2-2)) 
)

(:action move_p2-1_p2-3
:precondition (and (adj_p2-1_p2-3) (at_p2-1) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p2-1)) 
)

(:action grab_p2-1
:precondition (and (at_p2-1) (gold-at_p2-1) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p2-1)) 
)

(:action move_p2-1_p2-2
:precondition (and (adj_p2-1_p2-2) (at_p2-1) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p2-1)) 
)

(:action grab_p1-3
:precondition (and (at_p1-3) (gold-at_p1-3) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p1-3)) 
)

(:action grab_p1-2
:precondition (and (at_p1-2) (gold-at_p1-2) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p1-2)) 
)

(:action move_p2-1_p1-3
:precondition (and (adj_p2-1_p1-3) (at_p2-1) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p2-1)) 
)

(:action grab_p1-1
:precondition (and (at_p1-1) (gold-at_p1-1) (alive) )
:effect (got-the-treasure) 
(not (gold-at_p1-1)) 
)

(:action move_p2-1_p1-2
:precondition (and (adj_p2-1_p1-2) (at_p2-1) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p2-1)) 
)

(:action move_p2-1_p1-1
:precondition (and (adj_p2-1_p1-1) (at_p2-1) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p2-1)) 
)

(:action feel-breeze_p3-3
:precondition (and (alive) (at_p3-3) )
:effect (breeze_p3-3) 
() 
)

(:action feel-breeze_p3-2
:precondition (and (alive) (at_p3-2) )
:effect (breeze_p3-2) 
() 
)

(:action feel-breeze_p3-1
:precondition (and (alive) (at_p3-1) )
:effect (breeze_p3-1) 
() 
)

(:action move_p2-2_p3-3
:precondition (and (adj_p2-2_p3-3) (at_p2-2) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p2-2)) 
)

(:action move_p2-2_p3-2
:precondition (and (adj_p2-2_p3-2) (at_p2-2) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p2-2)) 
)

(:action feel-breeze_p2-3
:precondition (and (alive) (at_p2-3) )
:effect (breeze_p2-3) 
() 
)

(:action move_p2-2_p3-1
:precondition (and (adj_p2-2_p3-1) (at_p2-2) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p2-2)) 
)

(:action feel-breeze_p2-2
:precondition (and (alive) (at_p2-2) )
:effect (breeze_p2-2) 
() 
)

(:action feel-breeze_p2-1
:precondition (and (alive) (at_p2-1) )
:effect (breeze_p2-1) 
() 
)

(:action move_p2-2_p2-3
:precondition (and (adj_p2-2_p2-3) (at_p2-2) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p2-2)) 
)

(:action feel-breeze_p1-3
:precondition (and (alive) (at_p1-3) )
:effect (breeze_p1-3) 
() 
)

(:action move_p2-2_p2-1
:precondition (and (adj_p2-2_p2-1) (at_p2-2) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p2-2)) 
)

(:action feel-breeze_p1-2
:precondition (and (alive) (at_p1-2) )
:effect (breeze_p1-2) 
() 
)

(:action feel-breeze_p1-1
:precondition (and (alive) (at_p1-1) )
:effect (breeze_p1-1) 
() 
)

(:action move_p2-2_p1-3
:precondition (and (adj_p2-2_p1-3) (at_p2-2) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p2-2)) 
)

(:action move_p2-2_p1-2
:precondition (and (adj_p2-2_p1-2) (at_p2-2) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p2-2)) 
)

(:action move_p2-2_p1-1
:precondition (and (adj_p2-2_p1-1) (at_p2-2) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p2-2)) 
)

(:action move_p2-3_p3-3
:precondition (and (adj_p2-3_p3-3) (at_p2-3) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p2-3)) 
)

(:action move_p2-3_p3-2
:precondition (and (adj_p2-3_p3-2) (at_p2-3) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p2-3)) 
)

(:action move_p2-3_p3-1
:precondition (and (adj_p2-3_p3-1) (at_p2-3) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p2-3)) 
)

(:action move_p2-3_p2-2
:precondition (and (adj_p2-3_p2-2) (at_p2-3) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p2-3)) 
)

(:action move_p2-3_p2-1
:precondition (and (adj_p2-3_p2-1) (at_p2-3) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p2-3)) 
)

(:action move_p2-3_p1-3
:precondition (and (adj_p2-3_p1-3) (at_p2-3) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p2-3)) 
)

(:action move_p2-3_p1-2
:precondition (and (adj_p2-3_p1-2) (at_p2-3) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p2-3)) 
)

(:action move_p2-3_p1-1
:precondition (and (adj_p2-3_p1-1) (at_p2-3) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p2-3)) 
)

(:action move_p3-1_p3-3
:precondition (and (adj_p3-1_p3-3) (at_p3-1) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p3-1)) 
)

(:action move_p3-1_p3-2
:precondition (and (adj_p3-1_p3-2) (at_p3-1) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p3-1)) 
)

(:action move_p3-1_p2-3
:precondition (and (adj_p3-1_p2-3) (at_p3-1) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p3-1)) 
)

(:action move_p3-1_p2-2
:precondition (and (adj_p3-1_p2-2) (at_p3-1) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p3-1)) 
)

(:action move_p3-1_p2-1
:precondition (and (adj_p3-1_p2-1) (at_p3-1) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p3-1)) 
)

(:action move_p3-1_p1-3
:precondition (and (adj_p3-1_p1-3) (at_p3-1) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p3-1)) 
)

(:action move_p3-1_p1-2
:precondition (and (adj_p3-1_p1-2) (at_p3-1) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p3-1)) 
)

(:action move_p3-1_p1-1
:precondition (and (adj_p3-1_p1-1) (at_p3-1) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p3-1)) 
)

(:action move_p1-1_p3-3
:precondition (and (adj_p1-1_p3-3) (at_p1-1) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p1-1)) 
)

(:action move_p1-1_p3-2
:precondition (and (adj_p1-1_p3-2) (at_p1-1) (alive) (safe_p3-2) )
:effect (at_p3-2) 
(not (at_p1-1)) 
)

(:action move_p1-1_p3-1
:precondition (and (adj_p1-1_p3-1) (at_p1-1) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p1-1)) 
)

(:action move_p1-1_p2-3
:precondition (and (adj_p1-1_p2-3) (at_p1-1) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p1-1)) 
)

(:action move_p1-1_p2-2
:precondition (and (adj_p1-1_p2-2) (at_p1-1) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p1-1)) 
)

(:action move_p1-1_p2-1
:precondition (and (adj_p1-1_p2-1) (at_p1-1) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p1-1)) 
)

(:action move_p3-2_p3-3
:precondition (and (adj_p3-2_p3-3) (at_p3-2) (alive) (safe_p3-3) )
:effect (at_p3-3) 
(not (at_p3-2)) 
)

(:action smell_wumpus_p3-3
:precondition (and (alive) (at_p3-3) )
:effect (stench_p3-3) 
() 
)

(:action move_p3-2_p3-1
:precondition (and (adj_p3-2_p3-1) (at_p3-2) (alive) (safe_p3-1) )
:effect (at_p3-1) 
(not (at_p3-2)) 
)

(:action smell_wumpus_p3-2
:precondition (and (alive) (at_p3-2) )
:effect (stench_p3-2) 
() 
)

(:action move_p1-1_p1-3
:precondition (and (adj_p1-1_p1-3) (at_p1-1) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p1-1)) 
)

(:action smell_wumpus_p3-1
:precondition (and (alive) (at_p3-1) )
:effect (stench_p3-1) 
() 
)

(:action move_p1-1_p1-2
:precondition (and (adj_p1-1_p1-2) (at_p1-1) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p1-1)) 
)

(:action move_p3-2_p2-3
:precondition (and (adj_p3-2_p2-3) (at_p3-2) (alive) (safe_p2-3) )
:effect (at_p2-3) 
(not (at_p3-2)) 
)

(:action move_p3-2_p2-2
:precondition (and (adj_p3-2_p2-2) (at_p3-2) (alive) (safe_p2-2) )
:effect (at_p2-2) 
(not (at_p3-2)) 
)

(:action smell_wumpus_p2-3
:precondition (and (alive) (at_p2-3) )
:effect (stench_p2-3) 
() 
)

(:action move_p3-2_p2-1
:precondition (and (adj_p3-2_p2-1) (at_p3-2) (alive) (safe_p2-1) )
:effect (at_p2-1) 
(not (at_p3-2)) 
)

(:action smell_wumpus_p2-2
:precondition (and (alive) (at_p2-2) )
:effect (stench_p2-2) 
() 
)

(:action smell_wumpus_p2-1
:precondition (and (alive) (at_p2-1) )
:effect (stench_p2-1) 
() 
)

(:action move_p3-2_p1-3
:precondition (and (adj_p3-2_p1-3) (at_p3-2) (alive) (safe_p1-3) )
:effect (at_p1-3) 
(not (at_p3-2)) 
)

(:action move_p3-2_p1-2
:precondition (and (adj_p3-2_p1-2) (at_p3-2) (alive) (safe_p1-2) )
:effect (at_p1-2) 
(not (at_p3-2)) 
)

(:action smell_wumpus_p1-3
:precondition (and (alive) (at_p1-3) )
:effect (stench_p1-3) 
() 
)

(:action move_p3-2_p1-1
:precondition (and (adj_p3-2_p1-1) (at_p3-2) (alive) (safe_p1-1) )
:effect (at_p1-1) 
(not (at_p3-2)) 
)

(:action smell_wumpus_p1-2
:precondition (and (alive) (at_p1-2) )
:effect (stench_p1-2) 
() 
)

(:action smell_wumpus_p1-1
:precondition (and (alive) (at_p1-1) )
:effect (stench_p1-1) 
() 
)

)
