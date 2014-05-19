--J_COMPANY(ID,DESCRIPTION,NAME,VERSION)
insert into J_COMPANY values (1,'AKURA Communication','AKURA',1);

--J_ROLE(ID,DESCRIPTION,NAME,VERSION)
insert into J_ROLE values (1,'Administrateur','ROLE_ADMIN',1),
						  (2,'Superviseur','ROLE_SUPERVISOR',1),
						  (3,'Gerant','ROLE_ACTIVITY_MANAGER',1);
						  
--J_USER
INSERT INTO J_USER VALUES (1,'agahfranckjanel@yahoo.fr','Franck Janel','AGAH','7297aa334c4d70e3c71ae72a5dae5ddf180c456d8e3da50d5854e2162389347f','08 74 39 52','ADMINISTRATEUR','admin',4);						  
							
--J_ROLE_J_USER(JROLE_ID,USERS_ID)							
insert into  J_ROLE_J_USER values (1,1);

--J_USER_J_ROLE(JUSER_ID,ROLES_ID)							
insert into  J_USER_J_ROLE values (1,1);

--J_USER_J_COMPANY(JUSER_ID,COMPANIES_ID)
insert into  J_USER_J_COMPANY values (1,1);

--J_COMPANY_J_USER
insert into  J_COMPANY_J_USER values (1,1);