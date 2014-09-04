CREATE TABLE fiscalizacao
(
	idfiscalizacao INTEGER PRIMARY KEY ASC, 
	
	municipio TEXT,
	
	estado TEXT,
	
	zonaeleitoral TEXT,
	
	localdavotacao TEXT,
	
	secaoeleitoral TEXT,
	
	podeenviarrededados INTEGER,
	
	statusdoenvio INTEGER,
	
	data INTEGER
);

CREATE TABLE picturepath
(
	idpicturepath INTEGER PRIMARY KEY ASC, 
	idfiscalizacao INTEGER,
	picturepath TEXT
);

CREATE TABLE picturethirtypcpath
(
	idpicturethirtypcpath INTEGER PRIMARY KEY ASC, 
	idfiscalizacao INTEGER,
	picturethirtypcpath TEXT
);

CREATE TABLE pictureurl
(
	idpictureurl INTEGER PRIMARY KEY ASC, 
	idfiscalizacao INTEGER,
	pictureurl TEXT
);