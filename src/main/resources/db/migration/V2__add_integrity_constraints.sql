ALTER TABLE `user`
    ADD CONSTRAINT `chk_user_role` CHECK (`role` IN (0, 1));

ALTER TABLE `animal`
    ADD CONSTRAINT `chk_animal_type` CHECK (`type` IN (1, 2)),
    ADD CONSTRAINT `chk_animal_deleted` CHECK (`is_deleted` IN (0, 1));

ALTER TABLE `check_in`
    ADD CONSTRAINT `chk_check_in_anonymous` CHECK (`is_anonymous` IN (0, 1)),
    ADD CONSTRAINT `fk_check_in_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT,
    ADD CONSTRAINT `fk_check_in_animal` FOREIGN KEY (`animal_id`) REFERENCES `animal` (`id`) ON UPDATE RESTRICT ON DELETE RESTRICT;
