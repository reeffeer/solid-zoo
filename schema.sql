-- 1. Аккаунты и персонажи
CREATE TABLE account (
    account_id   INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login   TIMESTAMP    NULL
);

CREATE TABLE race (
    race_id      INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  TEXT         NULL,
    base_strength INTEGER     NOT NULL DEFAULT 10,
    base_agility  INTEGER     NOT NULL DEFAULT 10,
    base_intellect INTEGER    NOT NULL DEFAULT 10
);

CREATE TABLE character_class (
    class_id     INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    description  TEXT         NULL,
    base_health  INTEGER      NOT NULL DEFAULT 100,
    base_mana    INTEGER      NOT NULL DEFAULT 50
);

CREATE TABLE "character" (
    character_id INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id   INTEGER      NOT NULL REFERENCES account(account_id) ON DELETE CASCADE,
    race_id      INTEGER      NOT NULL REFERENCES race(race_id),
    class_id     INTEGER      NOT NULL REFERENCES character_class(class_id),
    name         VARCHAR(50)  NOT NULL,
    level        INTEGER      NOT NULL DEFAULT 1 CHECK (level >= 1 AND level <= 100),
    experience   BIGINT       NOT NULL DEFAULT 0,
    health       INTEGER      NOT NULL DEFAULT 100,
    mana         INTEGER      NOT NULL DEFAULT 50,
    gold         INTEGER      NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (account_id, name)
);

-- 2. Мир и локации
CREATE TABLE zone (
    zone_id      INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(100) NOT NULL,
    level_min    INTEGER      NOT NULL DEFAULT 1,
    level_max    INTEGER      NOT NULL DEFAULT 100,
    parent_zone_id INTEGER    NULL REFERENCES zone(zone_id)
);

CREATE TABLE character_location (
    character_id INTEGER      NOT NULL PRIMARY KEY REFERENCES "character"(character_id) ON DELETE CASCADE,
    zone_id      INTEGER      NOT NULL REFERENCES zone(zone_id),
    pos_x        DECIMAL(12,4) NOT NULL DEFAULT 0,
    pos_y        DECIMAL(12,4) NOT NULL DEFAULT 0,
    pos_z        DECIMAL(12,4) NOT NULL DEFAULT 0
);

-- 3. Предметы и инвентарь
CREATE TABLE item_type (
    item_type_id INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(50)  NOT NULL UNIQUE
);

CREATE TABLE item_template (
    item_template_id INTEGER  NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item_type_id     INTEGER  NOT NULL REFERENCES item_type(item_type_id),
    name             VARCHAR(100) NOT NULL,
    description      TEXT         NULL,
    level_required   INTEGER      NOT NULL DEFAULT 1,
    stack_max        INTEGER      NOT NULL DEFAULT 1 CHECK (stack_max >= 1),
    buy_price        INTEGER      NOT NULL DEFAULT 0,
    sell_price       INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE item_instance (
    item_instance_id INTEGER   NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item_template_id INTEGER   NOT NULL REFERENCES item_template(item_template_id),
    quantity         INTEGER   NOT NULL DEFAULT 1 CHECK (quantity >= 1),
    durability_current INTEGER NULL
);

CREATE TABLE inventory_slot (
    character_id    INTEGER NOT NULL REFERENCES "character"(character_id) ON DELETE CASCADE,
    slot_index      INTEGER NOT NULL CHECK (slot_index >= 0),
    item_instance_id INTEGER NULL REFERENCES item_instance(item_instance_id) ON DELETE SET NULL,
    PRIMARY KEY (character_id, slot_index)
);

-- 4. Умения
CREATE TABLE skill_template (
    skill_id     INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(100) NOT NULL,
    description  TEXT         NULL,
    cooldown_sec INTEGER     NOT NULL DEFAULT 0,
    mana_cost    INTEGER     NOT NULL DEFAULT 0,
    damage_base  INTEGER     NOT NULL DEFAULT 0,
    skill_type   VARCHAR(20) NOT NULL CHECK (skill_type IN ('active', 'passive'))
);

CREATE TABLE class_skill (
    class_id       INTEGER NOT NULL REFERENCES character_class(class_id) ON DELETE CASCADE,
    skill_id       INTEGER NOT NULL REFERENCES skill_template(skill_id) ON DELETE CASCADE,
    level_required INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (class_id, skill_id)
);

CREATE TABLE character_skill (
    character_id INTEGER   NOT NULL REFERENCES "character"(character_id) ON DELETE CASCADE,
    skill_id     INTEGER   NOT NULL REFERENCES skill_template(skill_id) ON DELETE CASCADE,
    skill_level  INTEGER   NOT NULL DEFAULT 1 CHECK (skill_level >= 1),
    learned_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (character_id, skill_id)
);

-- 5. Квесты
CREATE TABLE quest_template (
    quest_id           INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name               VARCHAR(200) NOT NULL,
    description        TEXT         NULL,
    zone_id            INTEGER      NULL REFERENCES zone(zone_id),
    level_required     INTEGER      NOT NULL DEFAULT 1,
    experience_reward  INTEGER      NOT NULL DEFAULT 0,
    gold_reward        INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE character_quest (
    character_id    INTEGER   NOT NULL REFERENCES "character"(character_id) ON DELETE CASCADE,
    quest_id        INTEGER   NOT NULL REFERENCES quest_template(quest_id) ON DELETE CASCADE,
    status          VARCHAR(20) NOT NULL CHECK (status IN ('started', 'completed', 'failed')),
    progress_current INTEGER   NOT NULL DEFAULT 0,
    accepted_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at    TIMESTAMP NULL,
    PRIMARY KEY (character_id, quest_id)
);

-- 6. Гильдии
CREATE TABLE guild_rank (
    rank_id   INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name      VARCHAR(50)  NOT NULL UNIQUE,
    can_invite BOOLEAN     NOT NULL DEFAULT FALSE,
    can_promote BOOLEAN    NOT NULL DEFAULT FALSE,
    can_kick   BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE guild (
    guild_id            INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name                VARCHAR(100) NOT NULL UNIQUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    leader_character_id INTEGER      NOT NULL REFERENCES "character"(character_id) ON DELETE RESTRICT
);

CREATE TABLE guild_member (
    guild_id     INTEGER   NOT NULL REFERENCES guild(guild_id) ON DELETE CASCADE,
    character_id INTEGER   NOT NULL REFERENCES "character"(character_id) ON DELETE CASCADE,
    rank_id      INTEGER   NOT NULL REFERENCES guild_rank(rank_id),
    joined_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (guild_id, character_id)
);

-- 7. NPC и монстры
CREATE TABLE npc_template (
    npc_id    INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name      VARCHAR(100) NOT NULL,
    zone_id   INTEGER      NULL REFERENCES zone(zone_id),
    npc_type  VARCHAR(30)  NOT NULL CHECK (npc_type IN ('vendor', 'quest_giver', 'trainer', 'neutral'))
);

CREATE TABLE monster_template (
    monster_id         INTEGER      NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name               VARCHAR(100) NOT NULL,
    zone_id            INTEGER      NULL REFERENCES zone(zone_id),
    level              INTEGER      NOT NULL DEFAULT 1,
    health             INTEGER      NOT NULL DEFAULT 100,
    experience_reward  INTEGER      NOT NULL DEFAULT 0,
    gold_reward        INTEGER      NOT NULL DEFAULT 0
);

CREATE TABLE loot_table (
    monster_id      INTEGER NOT NULL REFERENCES monster_template(monster_id) ON DELETE CASCADE,
    item_template_id INTEGER NOT NULL REFERENCES item_template(item_template_id) ON DELETE CASCADE,
    drop_chance      DECIMAL(5,4) NOT NULL CHECK (drop_chance >= 0 AND drop_chance <= 1),
    min_quantity     INTEGER NOT NULL DEFAULT 1 CHECK (min_quantity >= 0),
    max_quantity     INTEGER NOT NULL DEFAULT 1 CHECK (max_quantity >= min_quantity),
    PRIMARY KEY (monster_id, item_template_id)
);

-- 8. Торговля NPC
CREATE TABLE npc_vendor_item (
    npc_id           INTEGER NOT NULL REFERENCES npc_template(npc_id) ON DELETE CASCADE,
    item_template_id INTEGER NOT NULL REFERENCES item_template(item_template_id) ON DELETE CASCADE,
    price            INTEGER NOT NULL DEFAULT 0,
    stock            INTEGER NULL,
    PRIMARY KEY (npc_id, item_template_id)
);