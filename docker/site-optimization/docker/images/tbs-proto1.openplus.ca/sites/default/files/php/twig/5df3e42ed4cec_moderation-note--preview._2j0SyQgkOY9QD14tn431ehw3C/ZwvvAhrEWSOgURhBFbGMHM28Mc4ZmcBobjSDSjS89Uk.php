<?php

use Twig\Environment;
use Twig\Error\LoaderError;
use Twig\Error\RuntimeError;
use Twig\Markup;
use Twig\Sandbox\SecurityError;
use Twig\Sandbox\SecurityNotAllowedTagError;
use Twig\Sandbox\SecurityNotAllowedFilterError;
use Twig\Sandbox\SecurityNotAllowedFunctionError;
use Twig\Source;
use Twig\Template;

/* modules/contrib/moderation_note/templates/moderation-note--preview.html.twig */
class __TwigTemplate_d61282ea60af2befd902a734f6511a7d7832d2e68d527a535a14540cfd7f9a00 extends \Twig\Template
{
    public function __construct(Environment $env)
    {
        parent::__construct($env);

        $this->parent = false;

        $this->blocks = [
        ];
        $this->sandbox = $this->env->getExtension('\Twig\Extension\SandboxExtension');
        $tags = ["if" => 32];
        $filters = ["t" => 33, "nl2br" => 41];
        $functions = [];

        try {
            $this->sandbox->checkSecurity(
                ['if'],
                ['t', 'nl2br'],
                []
            );
        } catch (SecurityError $e) {
            $e->setSourceContext($this->getSourceContext());

            if ($e instanceof SecurityNotAllowedTagError && isset($tags[$e->getTagName()])) {
                $e->setTemplateLine($tags[$e->getTagName()]);
            } elseif ($e instanceof SecurityNotAllowedFilterError && isset($filters[$e->getFilterName()])) {
                $e->setTemplateLine($filters[$e->getFilterName()]);
            } elseif ($e instanceof SecurityNotAllowedFunctionError && isset($functions[$e->getFunctionName()])) {
                $e->setTemplateLine($functions[$e->getFunctionName()]);
            }

            throw $e;
        }

    }

    protected function doDisplay(array $context, array $blocks = [])
    {
        // line 31
        echo "<div ";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["attributes"] ?? null), "addClass", [0 => "moderation-note-preview"], "method")), "html", null, true);
        echo ">
    ";
        // line 32
        if (( !($context["published"] ?? null) &&  !($context["parent"] ?? null))) {
            // line 33
            echo "      <div class=\"moderation-note-publishing-status\">";
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->renderVar(t("Resolved"));
            echo "</div>
    ";
        }
        // line 35
        echo "    ";
        if (($context["quote"] ?? null)) {
            // line 36
            echo "      <div class=\"moderation-note-quote-information\">
          <blockquote>";
            // line 37
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["quote"] ?? null)), "html", null, true);
            echo "</blockquote>
      </div>
    ";
        }
        // line 40
        echo "    <div class=\"moderation-note-text\">
        ";
        // line 41
        echo nl2br($this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["text"] ?? null)), "html", null, true));
        echo "
    </div>
    <div class=\"moderation-note-owner\">
        ";
        // line 44
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["author_link"] ?? null)), "html", null, true);
        echo " ";
        echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed(($context["created_pretty"] ?? null)), "html", null, true);
        echo "
    </div>
    ";
        // line 46
        if ((($context["actions"] ?? null) && $this->getAttribute(($context["actions"] ?? null), "view", []))) {
            // line 47
            echo "      <div class=\"moderation-note-actions\">
        ";
            // line 48
            echo $this->env->getExtension('Drupal\Core\Template\TwigExtension')->escapeFilter($this->env, $this->sandbox->ensureToStringAllowed($this->getAttribute(($context["actions"] ?? null), "view", [])), "html", null, true);
            echo "
      </div>
    ";
        }
        // line 51
        echo "</div>
";
    }

    public function getTemplateName()
    {
        return "modules/contrib/moderation_note/templates/moderation-note--preview.html.twig";
    }

    public function isTraitable()
    {
        return false;
    }

    public function getDebugInfo()
    {
        return array (  107 => 51,  101 => 48,  98 => 47,  96 => 46,  89 => 44,  83 => 41,  80 => 40,  74 => 37,  71 => 36,  68 => 35,  62 => 33,  60 => 32,  55 => 31,);
    }

    /** @deprecated since 1.27 (to be removed in 2.0). Use getSourceContext() instead */
    public function getSource()
    {
        @trigger_error('The '.__METHOD__.' method is deprecated since version 1.27 and will be removed in 2.0. Use getSourceContext() instead.', E_USER_DEPRECATED);

        return $this->getSourceContext()->getCode();
    }

    public function getSourceContext()
    {
        return new Source("{#
/**
 * @file
 * \"Preview\" theme implementation for the moderation note entity.
 *
 * The only reason this exists as a distinct template is so that in the future
 * we can have more control over how notes look when viewed when the
 * \"View Notes\" button is clicked.
 *
 * Available variables:
 * - attributes: HTML attributes for the containing element.
 * - text: The plain text of the note.
 *   Using the nl2br Twig filter is recommended as the text may contain \"\\n\".
 * - quote: The text that was selected when creating this note.
 * - created: The timestamp when this note was created.
 * - created_pretty: The created time, formatted to be human-readable.
 * - updated: The timestamp when this note was updated.
 * - updated_pretty: The updated time, formatted to be human-readable.
 * - author_name: The display name of the author.
 * - author_link: A link to the author, as rendered by \$user->toLink().
 * - author_username: The username of the author.
 * - author_picture: The author's picture as rendered by the compact view mode.
 * - parent: The note's parent, if one exists.
 * - moderated_entity_link: A link to the entity this note is related to.
 * - actions: An array of buttons which open entity forms.
 * - published: Whether or not the note is published.
 *
 * @ingroup themeable
 */
#}
<div {{ attributes.addClass('moderation-note-preview') }}>
    {% if not published and not parent %}
      <div class=\"moderation-note-publishing-status\">{{ 'Resolved' | t }}</div>
    {% endif %}
    {% if quote %}
      <div class=\"moderation-note-quote-information\">
          <blockquote>{{ quote }}</blockquote>
      </div>
    {% endif %}
    <div class=\"moderation-note-text\">
        {{ text|nl2br }}
    </div>
    <div class=\"moderation-note-owner\">
        {{ author_link }} {{ created_pretty }}
    </div>
    {% if actions and actions.view %}
      <div class=\"moderation-note-actions\">
        {{ actions.view }}
      </div>
    {% endif %}
</div>
", "modules/contrib/moderation_note/templates/moderation-note--preview.html.twig", "/var/www/html/tbs-proto1.openplus.ca/modules/contrib/moderation_note/templates/moderation-note--preview.html.twig");
    }
}
