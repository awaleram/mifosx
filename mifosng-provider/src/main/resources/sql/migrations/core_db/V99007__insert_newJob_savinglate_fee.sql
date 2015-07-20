INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, 
                   `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES 
				   ('Apply Saving Deposite Late Fee', 'Apply Saving Deposite Late Fee', '0 0 12 1 1/1 ? *', now(), 5, NULL, NULL, 
				   NULL, 'Apply Saving Deposite Late FeeJobDetail15 _ DEFAULT', NULL, 1, 0, 1, 0, 0);
