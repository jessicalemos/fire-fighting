(deftemplate agente
	(slot combustivel)
	(slot id)
	(slot x)
	(slot y)
	(slot agua)
)

(deftemplate disponivel
	(slot valor)
	(slot id)
)

(deftemplate combate
	(slot x (type INTEGER))
		(slot combustivel)
	(slot y (type INTEGER))
	(slot id (type INTEGER))
		(slot agua)
		(slot tipo)
		(slot velocidade)
		(slot consumo)
	(slot disponivel))
	

(defrule updateDisp
	?dis <- (disponivel)
	?cb <- (combate {(id == dis.id)})
	=>
	(bind ?cb.disponivel ?dis.valor)
	(retract ?dis)
)

(defrule updateCombate
	?ag <- (agente)
	?cb <- (combate {(id == ag.id)})
	=>
	(bind ?cb.x ?ag.x)
	(bind ?cb.y ?ag.y)
	(bind ?cb.agua ?ag.agua)
	(bind ?cb.combustivel ?ag.combustivel)
	(retract ?ag)
)


(defquery procuraAgenteCombate
	(combate (x ?x)(y ?y)(id ?id)(agua ?a)(combustivel ?c)(disponivel ?d)(tipo ?t)(velocidade ?v)(consumo ?co))
)