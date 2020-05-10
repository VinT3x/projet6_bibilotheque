INSERT INTO categories (category_id,label) VALUES (1,'Roman');
INSERT INTO categories (category_id, label) VALUES (2, 'Art et Culture');
INSERT INTO categories (category_id, label) VALUES (3, 'Bande dessinée');
INSERT INTO categories (category_id, label) VALUES (4, 'Enseignement et Education');
INSERT INTO categories (category_id, label) VALUES (5, 'Santé et Bien-être');
INSERT INTO categories (category_id, label) VALUES (6, 'Histoire et Géographie');
INSERT INTO categories (category_id, label) VALUES (7, 'Jeunesse');
INSERT INTO categories (category_id, label) VALUES (8, 'Langues');

INSERT INTO authors (author_id,fullname, date_of_birth, date_of_death, nationality) VALUES (4,'Auteur Prénom','1900-12-12', null, 'Nationalité');
INSERT INTO books (category_id, date_official_release, NUMBER_OF_COPIES_FOR_RESERVATION, number_available, number_reservation_available, number_of_copies, number_of_page, summary, title, author_id) VALUES (1, '2019-05-02', 4, 8, 8, 4, 552, 'Luca. Quatre petites lettres pour l’un des romans les plus nerveux et les plus âpres de son auteur. Franck Thilliez ancre son intrigue dans l’actualité. Cette actualité sensible qui secoue régulièrement le quotidien des français : la GPA, le terrorisme, les affaires des grandes sociétés du numérique… C’est peu de dire que l’on vit jusqu’à la dernière ligne une aventure singulière qui marque durablement. L’équipe du commandant Sharko assure toujours autant quand il s’agit de résoudre les affaires délicates. Rien que pour ça, on est prêts à les suivre partout où ils iront !', 'Luca', 4);




