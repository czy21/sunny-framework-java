CREATE TABLE `file_target` (
    `key` varchar(100) NOT NULL,
    `root` varchar(255) NOT NULL COMMENT '',
    `path` varchar(255) NOT NULL COMMENT '',
    `kind` varchar(100) NOT NULL COMMENT 'OSS;LOCAL',
    access_key varchar(100) NULL COMMENT '',
    access_secret varchar(100) NULL COMMENT '',
    PRIMARY KEY (`key`)
) COMMENT='文件目标表';

CREATE TABLE `file` (
`id` varchar(36) NOT NULL,
`name` varchar(200) NOT NULL COMMENT '',
`path` varchar(200) NOT NULL COMMENT '',
`type` varchar(200) NOT NULL COMMENT '',
`file_target_key` varchar(100) NOT NULL COMMENT '',
PRIMARY KEY (`id`),
INDEX `fk_Material_MaterialTarget`(`file_target_key`)
) COMMENT='文件表';