

<html>
<head>
    <title>${name}</title>
    <style type="text/css">
        body, td, th{
            font-size: 12px;
            font-family: "Times New Roman", Times, serif;
        }

        .nowrap{
            white-space: nowrap;
        }

        .party_name{
            font-weight: bold;
            font-size: 16px;
            font-family: sans-serif;
            text-decoration: underline;
        }

        .template_name{
            font-weight: bold;
            width: 94%;
            text-align: left;
        }
        .top_note{
            width: 580px;
        }

        div.row_container{
            width: 880px;
            position: relative;
            page-break-inside: avoid;
            padding-bottom: 50px;
            vertical-align: top;
        }

        div.enemy_container{
            margin-top: 15px;
            margin-right: 0px;
            width: 350px;
            display: inline-block;
            page-break-inside: avoid;
            vertical-align: top;
        }

        .enemy_table{
            background: white;
            border-collapse: collapse;
        }

        .inner_enemy_table{
            border-collapse: collapse;
        }

        table#attributes {
            width: 150px;
        }

        table#characteristics {
            width: 40px;
        }

        table#hit_locations {
            width: 150px;
        }

        .inner_enemy_table td{
            padding: 2px;
        }

        .toprow{
            background: #555;
            color: white;
        }
        .evenrow{
            background: #CCC;
            line-height: 15px;
        }

        .oddrow{
            line-height: 15px;
        }

        .title{
            font-weight: bold;
        }

        .spirit_name{
            text-decoration: underline;
        }

        .item_list{
            font-style: italic;
        }

        table.weapon_table{
            border-spacing: 0px;
        }

        a {
            text-decoration: none;
            color: black;
        }

        a:hover{
            text-decoration: underline;
        }

        .page_break{
            page-break-after: always;
        }

        @page {
            margin: 0;
        }

    </style>
</head>
<body>
    <div id="enemies">
        <!-- Normal enemy begins -->
        <div class="enemy_container">
            <table class="enemy_table">
                <tr><td colspan="3">
                    <span id="enemy_1" class="template_name editable" contenteditable>${name}</span>
                </td></tr>
                <tr><td valign="top">
                    <table class="inner_enemy_table" id="characteristics">
                        <tr class="toprow"><th colspan="2">Chars</th></tr>

                        <tr class="oddrow">
                            <td>STR</td>
                            <td align="center">${stats[0]["STR"]}</td>
                        </tr>

                        <tr class="evenrow">
                            <td>CON</td>
                            <td align="center">${stats[1]["CON"]}</td>
                        </tr>

                        <tr class="oddrow">
                            <td>SIZ</td>
                            <td align="center">${stats[2]["SIZ"]}</td>
                        </tr>

                        <tr class="evenrow">
                            <td>DEX</td>
                            <td align="center">${stats[3]["DEX"]}</td>
                        </tr>

                        <tr class="oddrow">
                            <td>INT</td>
                            <td align="center">${stats[4]["INT"]}</td>
                        </tr>

                        <tr class="evenrow">
                            <td>POW</td>
                            <td align="center">${stats[5]["POW"]}</td>
                        </tr>

                        <tr class="oddrow">
                            <td>CHA</td>
                            <td align="center">${stats[6]["CHA"]}</td>
                        </tr>
                    </table>
                </td><td valign="top">
                    <table class="inner_enemy_table" id="attributes">
                        <tr class="toprow"> <th colspan="2">Attributes</th></tr>
                        <tr class="oddrow"> <td>Action&nbsp;Points</td>  <td>${attributes.action_points}</td></tr>
                        <tr class="evenrow"><td>Damage&nbsp;Modifier</td><td>${attributes.damage_modifier}</td></tr>
                        <tr class="oddrow"> <td>Magic&nbsp;Points</td>   <td>${attributes.magic_points}</td></tr>
                        <tr class="evenrow"><td>Movement</td>       <td>${attributes.movement}</td></tr>
                        <tr class="oddrow"> <td>Strike&nbsp;Rank</td>    <td>${attributes.strike_rank}</td></tr>
                        <tr class="evenrow"><td>Cult&nbsp;rank</td>      <td>${cult_rank}</td></tr>
                    </table>

                </td><td valign="top">
                    <table class="inner_enemy_table" id="hit_locations">
                        <tr class="toprow"><th>1D20</th><th>Location</th><th>AP/HP</th></tr>
                        <#list hit_locations as hl>
                        <#if hl_index % 2 == 0>
                            <tr class="oddrow">
                        <#else>
                            <tr class="evenrow">
                        </#if>
                                <td class="nowrap" align="center">${hl.range}</td>
                                <td>${hl.name}</td>
                                <td align="center">${hl.ap}/${hl.hp}</td>
                            </tr>
                        </#list>
                    </table>

                </td></tr>
            </table>
            <br>

            <#if cults?has_content>
                <span class="title">Cults:</span>
                <span class="item_list">
                    <#list cults as cult>
                        ${cult}<#if cult_has_next>,</#if>
                    </#list>
                </span>
                <br>
                <br>
            </#if>

            <span class="title">Combat Style:</span>

            <#list combat_styles as combat_style>
                <span class="item_list">${combat_style.name} ${combat_style.value}%</span>
                <table class="weapon_table">
                    <tr class="toprow"><th align="left">Weapon</th><th>Size/Force</th><th align="left">Reach</th><th align="left">Damage</th><th align="left">AP/HP</th><th align="left">Effects</th></tr>
                    <#list combat_style.weapons as weapon>
                        <#if weapon_index % 2 == 0>
                            <tr class="oddrow">
                        <#else>
                            <tr class="evenrow">
                        </#if>
                        <td>${weapon.name}</td>
                        <td align="center">${weapon.size}</td>
                        <td align="center">${weapon.reach}</td>
                        <td align="center">${weapon.damage}</td>
                        <td align="center">${weapon.ap}/${weapon.hp}</td>
                        <td>${weapon.effects}</td>
                    </tr>
                    </#list>
                </table>
            </#list>
            <br>

            <span class="title">Skills:</span><span class="item_list">
            <#list skills as skill>
                <#list skill?keys as k>
                    ${k} ${skill[k]}%<#if skill_has_next>,</#if>
                </#list>
            </#list>
            </span>
            <br>
            <br>

            <#if folk_spells?has_content>
                <span class="title">Folk magic spells:</span>
                <span class="item_list">
                    <#list folk_spells as spell>
                        ${spell}<#if spell_has_next>,</#if>
                    </#list>
                </span>
                <br>
                <br>
            </#if>

            <#if theism_spells?has_content>
                <span class="title">Theism miracles:</span>
                <span class="item_list">
                    <#list theism_spells as spell>
                        ${spell}<#if spell_has_next>,</#if>
                    </#list>
                </span>
                <br>
                <br>
            </#if>

            <#if sorcery_spells?has_content>
                <span class="title">Sorcery spells:</span>
                <span class="item_list">
                    <#list sorcery_spells as spell>
                        ${spell}<#if spell_has_next>,</#if>
                    </#list>
                </span>
                <br>
                <br>
            </#if>

            <#if spirits?has_content>
                <span class="title">Spirits:</span>
                <span class="item_list">
                    <#list spirits as spell>
                        ${spell}<#if spell_has_next>,</#if>
                    </#list>
                </span>
                <br>
                <br>
            </#if>

            <#if features?has_content>
                <span class="title">Features:</span><span class="item_list">
                <#list features as feature>
                    <div class="features">
                        ${feature}
                    </div>
                </#list>
                <br>
            </#if>

            <#if notes?has_content>
                <span class="title">Notes:</span><span class="item_list">${notes}</span>
                <br>
            </#if>

        </div>
    </div>
</body>
</html>